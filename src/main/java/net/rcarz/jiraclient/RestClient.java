/**
 * jira-client - a simple JIRA REST client
 * Copyright (c) 2013 Bob Carroll (bob.carroll@alum.rit.edu)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.rcarz.jiraclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;

/**
 * A simple REST client that speaks JSON.
 */
public class RestClient {
    
    private HttpClient httpClient = null;
    private ICredentials creds = null;
    private URI uri = null;

    /**
     * Creates a REST client instance with a URI.
     *
     * @param httpclient Underlying HTTP client to use
     * @param uri Base URI of the remote REST service
     */
    public RestClient(HttpClient httpclient, URI uri) {
        this(httpclient, null, uri);
    }

    /**
     * Creates an authenticated REST client instance with a URI.
     *
     * @param httpclient Underlying HTTP client to use
     * @param creds Credentials to send with each request
     * @param uri Base URI of the remote REST service
     */
    public RestClient(HttpClient httpclient, ICredentials creds, URI uri) {
        this.httpClient = httpclient;
        this.creds = creds;
        this.uri = uri;
    }
    
    /**
     * Build a URI from a path.
     *
     * @param path Path to append to the base URI
     *
     * @return the full URI
     *
     * @throws URISyntaxException when the path is invalid
     */
    public URI buildURI(String... pathParts) throws URISyntaxException {
        return buildURI(null, pathParts);
    }

    /**
     * Build a URI from a path and query parmeters.
     *
     * @param path Path to append to the base URI
     * @param params Map of key value pairs
     *
     * @return the full URI
     *
     * @throws URISyntaxException when the path is invalid
     */
    public URI buildURI(Map<String, String> params, String... pathParts) throws URISyntaxException {
        URIBuilder ub = new URIBuilder(uri);
        String path = "";
        for (String part : pathParts) {
        	path += "/" + part;
        }
        ub.setPath(ub.getPath() + path);

        if (params != null) {
            for (Map.Entry<String, String> ent : params.entrySet())
                ub.addParameter(ent.getKey(), ent.getValue());
        }

        return ub.build();
    }

    private JSON request(HttpRequestBase req) throws RestException, IOException, AuthenticationException {
        req.addHeader("Accept", "application/json");

        if (creds != null)
            creds.authenticate(req);

        HttpResponse resp = httpClient.execute(req);
        HttpEntity ent = resp.getEntity();
        StringBuilder result = new StringBuilder();

        if (ent != null) {
            String encoding = null;
            if (ent.getContentEncoding() != null) {
            	encoding = ent.getContentEncoding().getValue();
            }
            
            if (encoding == null) {
    	        Header contentTypeHeader = resp.getFirstHeader("Content-Type");
    	        HeaderElement[] contentTypeElements = contentTypeHeader.getElements();
    	        for (HeaderElement he : contentTypeElements) {
    	        	NameValuePair nvp = he.getParameterByName("charset");
    	        	if (nvp != null) {
    	        		encoding = nvp.getValue();
    	        	}
    	        }
            }
            
            InputStreamReader isr =  encoding != null ?
                new InputStreamReader(ent.getContent(), encoding) :
                new InputStreamReader(ent.getContent());
            BufferedReader br = null;

            try {
            	String line = "";
            	br = new BufferedReader(isr);
	            while ((line = br.readLine()) != null)
	                result.append(line);
            } finally {
            	if (br != null) {
            		br.close();
            	}
            }
        }

        StatusLine sl = resp.getStatusLine();

        if (sl.getStatusCode() >= 300)
            throw new RestException(sl.getReasonPhrase(), sl.getStatusCode(), result.toString());

        return result.length() > 0 ? JSONSerializer.toJSON(result.toString()): null;
    }

    private JSON request(HttpEntityEnclosingRequestBase req, String payload) 
    		throws RestException, IOException, AuthenticationException {

        if (payload != null) {
            StringEntity ent = null;

            try {
                ent = new StringEntity(payload, "UTF-8");
                ent.setContentType("application/json");
            } catch (UnsupportedCharsetException ex) {
                /* utf-8 should always be supported... */
            }

            req.addHeader("Content-Type", "application/json");
            req.setEntity(ent);
        }

        return request(req);
    }
    
    private JSON request(HttpEntityEnclosingRequestBase req, File file) 
    		throws RestException, IOException, AuthenticationException {
        
    	if (file != null) {
            File fileUpload = file;
            req.setHeader("X-Atlassian-Token", "nocheck");
            MultipartEntityBuilder ent = MultipartEntityBuilder.create();
            ent.addPart("file", new FileBody(fileUpload));
            req.setEntity(ent.build());
        }
        return request(req);
    }

    private JSON request(HttpEntityEnclosingRequestBase req, Issue.NewAttachment... attachments) 
    		throws RestException, IOException, AuthenticationException {
        
    	if (attachments != null) {
            req.setHeader("X-Atlassian-Token", "nocheck");
            MultipartEntityBuilder ent = MultipartEntityBuilder.create();
            for(Issue.NewAttachment attachment : attachments) {
                String filename = attachment.getFilename();
                Object content = attachment.getContent();
                if (content instanceof byte[]) {
                    ent.addPart("file", new ByteArrayBody((byte[]) content, filename));
                } else if (content instanceof InputStream) {
                    ent.addPart("file", new InputStreamBody((InputStream) content, filename));
                } else if (content instanceof File) {
                    ent.addPart("file", new FileBody((File) content));
                } else if (content == null) {
                    throw new IllegalArgumentException("Missing content for the file " + filename);
                } else {
                    throw new IllegalArgumentException(
                        "Expected file type byte[], java.io.InputStream or java.io.File but provided " +
                            content.getClass().getName() + " for the file " + filename);
                }
            }
            req.setEntity(ent.build());
        }
        return request(req);
    }

    private JSON request(HttpEntityEnclosingRequestBase req, JSON payload) 
    		throws RestException, IOException, AuthenticationException {

        return request(req, payload != null ? payload.toString() : null);
    }

    /**
     * Executes an HTTP DELETE with the given URI.
     *
     * @param uri Full URI of the remote endpoint
     *
     * @return JSON-encoded result or null when there's no content returned
     *
     * @throws RestException when an HTTP-level error occurs
     * @throws IOException when an error reading the response occurs
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> delete(URI uri) 
    		throws RestException, IOException, AuthenticationException, JiraException {
        
    	return JSONtoMap(request(new HttpDelete(uri)));
    }

    /**
     * Executes an HTTP DELETE with the given path.
     *
     * @param path Path to be appended to the URI supplied in the construtor
     *
     * @return JSON-encoded result or null when there's no content returned
     *
     * @throws RestException when an HTTP-level error occurs
     * @throws IOException when an error reading the response occurs
     * @throws URISyntaxException when an error occurred appending the path to the URI
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> delete(String... pathParts) 
    		throws RestException, IOException, URISyntaxException, AuthenticationException, JiraException {
        
    	return delete(buildURI(pathParts));
    }
    
    public Map<String, Object> delete(Map<String, String> params, String... pathParts) 
    		throws RestException, IOException, URISyntaxException, AuthenticationException, JiraException {
        
    	return delete(buildURI(params, pathParts));
    }

    /**
     * Executes an HTTP GET with the given URI.
     *
     * @param uri Full URI of the remote endpoint
     *
     * @return JSON-encoded result or null when there's no content returned
     *
     * @throws RestException when an HTTP-level error occurs
     * @throws IOException when an error reading the response occurs
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> get(URI uri) 
    		throws RestException, IOException, AuthenticationException, JiraException {
        
    	return JSONtoMap(request(new HttpGet(uri)));
    }

    /**
     * Executes an HTTP GET with the given path.
     *
     * @param path Path to be appended to the URI supplied in the construtor
     * @param params Map of key value pairs
     *
     * @return JSON-encoded result or null when there's no content returned
     *
     * @throws RestException when an HTTP-level error occurs
     * @throws IOException when an error reading the response occurs
     * @throws URISyntaxException when an error occurred appending the path to the URI
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> get(Map<String, String> params, String... pathParts) 
    		throws RestException, IOException, URISyntaxException, AuthenticationException, JiraException {
        
    	return get(buildURI(params, pathParts));
    }

    /**
     * Executes an HTTP GET with the given path.
     *
     * @param path Path to be appended to the URI supplied in the construtor
     *
     * @return JSON-encoded result or null when there's no content returned
     *
     * @throws RestException when an HTTP-level error occurs
     * @throws IOException when an error reading the response occurs
     * @throws URISyntaxException when an error occurred appending the path to the URI
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> get(String... pathParts) 
    		throws RestException, IOException, URISyntaxException, AuthenticationException, JiraException {
        
    	return get(null, pathParts);
    }


    /**
     * Executes an HTTP POST with the given URI and payload.
     *
     * @param uri Full URI of the remote endpoint
     * @param payload JSON-encoded data to send to the remote service
     *
     * @return JSON-encoded result or null when there's no content returned
     *
     * @throws RestException when an HTTP-level error occurs
     * @throws IOException when an error reading the response occurs
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> post(URI uri, JSON payload) 
    		throws RestException, IOException, AuthenticationException, JiraException {
        
    	return JSONtoMap(request(new HttpPost(uri), payload));
    }

    /**
     * Executes an HTTP POST with the given URI and payload.
     *
     * At least one JIRA REST endpoint expects malformed JSON. The payload
     * argument is quoted and sent to the server with the application/json
     * Content-Type header. You should not use this function when proper JSON
     * is expected.
     *
     * @see https://jira.atlassian.com/browse/JRA-29304
     *
     * @param uri Full URI of the remote endpoint
     * @param payload Raw string to send to the remote service
     *
     * @return JSON-encoded result or null when there's no content returned
     *
     * @throws RestException when an HTTP-level error occurs
     * @throws IOException when an error reading the response occurs
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> post(URI uri, String payload) 
    		throws RestException, IOException, AuthenticationException, JiraException {
    	
    	String quoted = null;
    	if (payload != null && !payload.equals(new JSONObject())) {
    		quoted = String.format("\"%s\"", payload);
    	}
        return JSONtoMap(request(new HttpPost(uri), quoted));
    }

    /**
     * Executes an HTTP POST with the given path and payload.
     *
     * @param path Path to be appended to the URI supplied in the construtor
     * @param payload JSON-encoded data to send to the remote service
     *
     * @return JSON-encoded result or null when there's no content returned
     *
     * @throws RestException when an HTTP-level error occurs
     * @throws IOException when an error reading the response occurs
     * @throws URISyntaxException when an error occurred appending the path to the URI
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> post(JSON payload, String... pathParts) 
    		throws RestException, IOException, URISyntaxException, AuthenticationException, JiraException {
        
    	return post(buildURI(pathParts), payload);
    }
    
    /**
     * Executes an HTTP POST with the given path.
     *
     * @param path Path to be appended to the URI supplied in the construtor
     *
     * @return JSON-encoded result or null when there's no content returned
     *
     * @throws RestException when an HTTP-level error occurs
     * @throws IOException when an error reading the response occurs
     * @throws URISyntaxException when an error occurred appending the path to the URI
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> post(String... pathParts) 
    		throws RestException, IOException, URISyntaxException, AuthenticationException, JiraException {
        
    	return post(buildURI(pathParts), new JSONObject());
    }
    
    /**
     * Executes an HTTP POST with the given path and file payload.
     * 
     * @param path Full URI of the remote endpoint
     * @param file java.io.File
     * 
     * @return JSON-encoded result or null when there's no content returned
     * 
     * @throws URISyntaxException 
     * @throws IOException 
     * @throws RestException 
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> post(File file, String... pathParts) 
    		throws RestException, IOException, URISyntaxException, AuthenticationException, JiraException {
        
    	return JSONtoMap(request(new HttpPost(buildURI(pathParts)), file));
    }

    /**
     * Executes an HTTP POST with the given path and file payloads.
     *
     * @param path    Full URI of the remote endpoint
     * @param attachments   the name of the attachment
     *
     *@return JSON-encoded result or null when there's no content returned
     *
     * @throws URISyntaxException
     * @throws IOException
     * @throws RestException
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> post(URI uri, Issue.NewAttachment... attachments) 
    		throws RestException, IOException, URISyntaxException, AuthenticationException, JiraException {
        
    	return JSONtoMap(request(new HttpPost(uri), attachments));
    }

    /**
     * Executes an HTTP PUT with the given URI and payload.
     *
     * @param uri Full URI of the remote endpoint
     * @param payload JSON-encoded data to send to the remote service
     *
     * @return JSON-encoded result or null when there's no content returned
     *
     * @throws RestException when an HTTP-level error occurs
     * @throws IOException when an error reading the response occurs
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> put(URI uri, JSON payload) 
    		throws RestException, IOException, AuthenticationException, JiraException {
        
    	return JSONtoMap(request(new HttpPut(uri), payload));
    }

    /**
     * Executes an HTTP PUT with the given path and payload.
     *
     * @param path Path to be appended to the URI supplied in the construtor
     * @param payload JSON-encoded data to send to the remote service
     *
     * @return JSON-encoded result or null when there's no content returned
     *
     * @throws RestException when an HTTP-level error occurs
     * @throws IOException when an error reading the response occurs
     * @throws URISyntaxException when an error occurred appending the path to the URI
     * @throws AuthenticationException 
     * @throws JiraException 
     */
    public Map<String, Object> put(JSON payload, String... pathParts) 
    		throws RestException, IOException, URISyntaxException, AuthenticationException, JiraException {
        
    	return put(buildURI(pathParts), payload);
    }
    
    /**
     * Exposes the http client.
     *
     * @return the httpClient property
     */
    public HttpClient getHttpClient(){
        return this.httpClient;
    }
    
    /**
     * Takes a JSON object and converts it to a Map
     * 
     * @param JSONObject
     * 
     * @return Map
     * 
     * @throws JSONException
     * @throws JiraException
     */
    public static Map<String, Object> JSONtoMap(Object object) throws JSONException, JiraException {
        Map<String, Object> map = new HashMap<String, Object>();

        if (object == null) {
        	return null;
        } else if (object instanceof JSONArray) {
    		map.put("data", JSONtoList(object));
        } else {
	        if (!(object instanceof JSONObject)) {
	            throw new JiraException("JSON payload is malformed");
	        }
	        
	        JSONObject jsonMap = (JSONObject) object;
	        
	        if (jsonMap.isNullObject()) {
	            throw new JiraException("JSON payload is empty");
	        }
	        
	        Iterator<?> keysItr = jsonMap.keys();
	        while (keysItr.hasNext()) {
	            String key = (String)keysItr.next();
	            Object value = jsonMap.get(key);
	
	            if (value instanceof JSONArray) {
	                value = JSONtoList(value);
	            } else if (value instanceof JSONObject) {
	                value = JSONtoMap(value);
	            }
	            
	            map.put(key, value);
	        }
        }
        
        return map;
    }

    /**
     * Takes a JSON array and converts it to a List
     * 
     * @param JSONArray
     * 
     * @return List
     * 
     * @throws JSONException
     * @throws JiraException
     */
    private static List<Object> JSONtoList(Object object) throws JSONException, JiraException {
        List<Object> list = new ArrayList<Object>();
        
        if (!(object instanceof JSONArray)) {
        	throw new JiraException("JSON payload is malformed");
        }
        
        JSONArray jsonArray = (JSONArray) object;
        
        for (Object value : jsonArray) {
            if (value instanceof JSONArray) {
                value = JSONtoList(value);
            } else if (value instanceof JSONObject) {
                value = JSONtoMap(value);
            }
            
            list.add(value);
        }
        
        return list;
    }
}
