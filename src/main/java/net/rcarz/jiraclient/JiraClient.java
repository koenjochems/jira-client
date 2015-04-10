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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rcarz.jiraclient.Issue.NewAttachment;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * A simple JIRA REST client.
 */
public class JiraClient extends AClient {

	public static final String SEARCH_URI = "search";
	public static final String CREATEMETA_URI = "createmeta";
	public static final String EDITMETA_URI = "editmeta";
	
	public static final String DEFAULT_API_REV = "latest";
    public static String apirev = DEFAULT_API_REV;
	
    private String username = null;

    /**
     * Issue search results structure.
     */
    public static class SearchResult {
        public int start = 0;
        public int max = 0;
        public int total = 0;
        public List<Issue> issues = null;
    }
    
    public abstract class AbstractFluent extends AClient {
		protected AbstractFluent(RestClient restclient) {
			super(restclient);
		}

		Map<String, Object> fields = new HashMap<String, Object>();
		
		/**
         * Appends a field to the update action.
         *
         * @param name Name of the field
         * @param value New field value
         *
         * @return the current fluent update instance
         */
        public void field(String name, Object value) {
            fields.put(name, value);
        }
	}
	
    /**
     * Used to chain fields to a create action.
     */
    public final class FluentCreateIssue extends AbstractFluent {
        Map<String, Object> createmeta = null;

        private FluentCreateIssue(RestClient restclient, Map<String, Object> map) {
        	super(restclient);
        	
            this.createmeta = map;
        }

        /**
         * Executes the create action (issue includes all fields).
         *
         * @throws JiraException when the create fails
         */
        public Issue execute() throws JiraException {
            return executeCreate(null);
        }

        /**
         * Executes the create action and specify which fields to retrieve.
         *
         * @param includedFields Specifies which issue fields will be included
         * in the result.
         * <br>Some examples how this parameter works:
         * <ul>
         * <li>*all - include all fields</li>
         * <li>*navigable - include just navigable fields</li>
         * <li>summary,comment - include just the summary and comments</li>
         * <li>*all,-comment - include all fields</li>
         * </ul>
         *
         * @throws JiraException when the create fails
         */
        public Issue execute(String includedFields) throws JiraException {
            return executeCreate(includedFields);
        }

        /**
         * Executes the create action and specify which fields to retrieve.
         *
         * @param includedFields Specifies which issue fields will be included
         * in the result.
         * <br>Some examples how this parameter works:
         * <ul>
         * <li>*all - include all fields</li>
         * <li>*navigable - include just navigable fields</li>
         * <li>summary,comment - include just the summary and comments</li>
         * <li>*all,-comment - include all fields</li>
         * </ul>
         *
         * @throws JiraException when the create fails
         */
        private Issue executeCreate(String includedFields) throws JiraException {
            JSONObject fieldmap = new JSONObject();

            if (fields.size() == 0) {
                throw new JiraException("No fields were given for create");
            }

            for (Map.Entry<String, Object> ent : fields.entrySet()) {
                Object newval = Field.toJson(ent.getKey(), ent.getValue(), createmeta);
                fieldmap.put(ent.getKey(), newval);
            }

            JSONObject req = new JSONObject();
            req.put("fields", fieldmap);

            Map<String, Object> data = null;

            try {
                data = restclient.post(req, getBaseUri(), Issue.URI);
            } catch (Exception ex) {
                throw new JiraException("Failed to create issue", ex);
            }

            if (!(data.get("key") instanceof String)) {
                throw new JiraException("Unexpected result on create issue");
            }

            return getIssue(Field.getString(data.get("key")), includedFields, null);
        }
    }

    /**
     * Used to chain fields to an update action.
     */
    public final class FluentUpdateIssue extends AbstractFluent {
        Map<String, List<Object>> fieldOpers = new HashMap<String, List<Object>>();
        Map<String, Object> editmeta = null;
        String key = null;

        private FluentUpdateIssue(RestClient restclient, Map<String, Object> editmeta, String issueKey) {
        	super(restclient);
        	
            this.editmeta = editmeta;
            this.key = issueKey;
        }

        /**
         * Executes the update action.
         *
         * @throws JiraException when the update fails
         */
        public void execute() throws JiraException {
            JSONObject fieldmap = new JSONObject();
            JSONObject updatemap = new JSONObject();

            if (fields.size() == 0 && fieldOpers.size() == 0)
                throw new JiraException("No fields were given for update");

            for (Map.Entry<String, Object> ent : fields.entrySet()) {
                Object newval = Field.toJson(ent.getKey(), ent.getValue(), editmeta);
                fieldmap.put(ent.getKey(), newval);
            }

            for (Map.Entry<String, List<Object>> ent : fieldOpers.entrySet()) {
                Object newval = Field.toJson(ent.getKey(), ent.getValue(), editmeta);
                updatemap.put(ent.getKey(), newval);
            }

            JSONObject req = new JSONObject();

            if (fieldmap.size() > 0)
                req.put("fields", fieldmap);

            if (updatemap.size() > 0)
                req.put("update", updatemap);

            try {
                restclient.put(req, getBaseUri(), Issue.URI, key);
            } catch (Exception ex) {
                throw new JiraException("Failed to update issue " + key, ex);
            }
        }

        private FluentUpdateIssue fieldOperation(String oper, String name, Object value) {
            if (!fieldOpers.containsKey(name))
                fieldOpers.put(name, new ArrayList<Object>());

            fieldOpers.get(name).add(new Field.Operation(oper, value));
            return this;
        }

        /**
         *  Adds a field value to the existing value set.
         *
         *  @param name Name of the field
         *  @param value Field value to append
         *
         *  @return the current fluent update instance
         */
        public FluentUpdateIssue fieldAdd(String name, Object value) {
            return fieldOperation("add", name, value);
        }

        /**
         *  Removes a field value from the existing value set.
         *
         *  @param name Name of the field
         *  @param value Field value to remove
         *
         *  @return the current fluent update instance
         */
        public FluentUpdateIssue fieldRemove(String name, Object value) {
            return fieldOperation("remove", name, value);
        }
    }

    /**
     * Used to chain fields to a transition action.
     */
    public final class FluentTransitionIssue extends AbstractFluent {
        List<Transition> transitions = null;
        String key = null;

        private FluentTransitionIssue(RestClient restclient, List<Transition> transitions, String issueKey) {
        	super(restclient);
        	
            this.transitions = transitions;
            this.key = issueKey;
        }

        private Transition getTransition(String id, boolean name) throws JiraException {
            Transition result = null;

            for (Transition transition : transitions) {
                if((name && id.equals(transition.getName()) || (!name && id.equals(transition.getId())))) {
                    result = transition;
                }
            }

            if (result == null)
                throw new JiraException("Transition was not found.");

            return result;
        }

        private void realExecute(Transition trans) throws JiraException {

            if (trans == null || trans.getFields() == null)
                throw new JiraException("Transition is missing fields");

            JSONObject fieldmap = new JSONObject();

            for (Map.Entry<String, Object> ent : fields.entrySet()) {
                fieldmap.put(ent.getKey(), ent.getValue());
            }

            JSONObject req = new JSONObject();

            if (fieldmap.size() > 0)
                req.put("fields", fieldmap);

            JSONObject t = new JSONObject();
            t.put("id", Field.getString(trans.getId()));

            req.put("transition", t);

            try {
                restclient.post(req, getBaseUri(), Issue.URI, key, Transition.URI);
            } catch (Exception ex) {
                throw new JiraException("Failed to transition issue " + key, ex);
            }
        }

        /**
         * Executes the transition action.
         *
         * @param id Internal transition ID
         *
         * @throws JiraException when the transition fails
         */
        public void execute(int id) throws JiraException {
            realExecute(getTransition(Integer.toString(id), false));
        }

        /**
         * Executes the transition action.
         *
         * @param transition Transition
         *
         * @throws JiraException when the transition fails
         */
        public void execute(Transition transition) throws JiraException {
            realExecute(transition);
        }

        /**
         * Executes the transition action.
         *
         * @param name Transition name
         *
         * @throws JiraException when the transition fails
         */
        public void execute(String name) throws JiraException {
            realExecute(getTransition(name, true));
        }
    }
    
    /**
     * Used to chain fields to a create action.
     */
    public final class FluentCreateComponent extends AbstractFluent {
        /**
         * Creates a new fluent.
         * @param restclient the REST client
         * @param project the project key
         */
        private FluentCreateComponent(RestClient restclient, String project) {
        	super(restclient);
        	
            field("project", project);
        }
        
        /**
         * Sets the name of the component.
         * @param name the name
         */
        public void name(String name) {
        	field("name", name);
        }
        
        /**
         * Sets the description of the component.
         * @param description the description
         */
        public void description(String description) {
        	field("description", description);
        }
        
        /**
         * Sets the lead user name.
         * @param leadUserName the lead user name
         */
        public void leadUserName(String leadUserName) {
        	field("leadUserName", leadUserName);
        }
        
        /**
         * Sets the assignee type.
         * @param assigneeType the assignee type
         */
        public void assigneeType(String assigneeType) {
        	field("assigneeType", assigneeType);
        }
        
        /**
         * Sets whether the assignee type is valid.
         * @param assigneeTypeValid is the assignee type valid?
         */
        public void assigneeTypeValue(boolean assigneeTypeValid) {
        	field("isAssigneeTypeValid", assigneeTypeValid);
        }
        
        /**
         * Executes the create action.
         * @return the created component
         *
         * @throws JiraException when the create fails
         */
        public Component execute() throws JiraException {
        	JSONObject req = new JSONObject();

            if (fields.size() == 0) {
                throw new JiraException("No fields were given for create");
            }

            for (Map.Entry<String, Object> ent : fields.entrySet()) {
                req.put(ent.getKey(), ent.getValue());
            }
            
        	Map<String, Object> data = null;

            try {
            	data = restclient.post(req, getBaseUri(), Component.URI);
            } catch (Exception ex) {
                throw new JiraException("Failed to create component", ex);
            }
            
            if (!(data.get("id") instanceof String)) {
                throw new JiraException("Unexpected result on create component");
            }

            return new Component(data);
        }
    }
    
    /**
     * Creates a JIRA client.
     *
     * @param uri Base URI of the JIRA server
     */
    public JiraClient(String uri) {
        this(uri, null);
    }

    /**
     * Creates an authenticated JIRA client.
     *
     * @param uri Base URI of the JIRA server
     * @param creds Credentials to authenticate with
     */
    public JiraClient(String uri, ICredentials creds) {
    	super(new RestClient(HttpClientBuilder.create().build(), creds, URI.create(uri)));

        if (creds != null) {
            username = creds.getLogonName();
        }
    }
    
    /**
     * Gets the JIRA REST API revision number.
     */
    public static String getApiRev() {
        return apirev;
    }

    /**
     * Sets the JIRA REST API revision number.
     */
    public static void setApiRev(String apirev) {
    	JiraClient.apirev = apirev;
    }

    /**
     * Resource base URI with API revision number.
     */
    public static String getBaseUri() {
        return String.format("rest/api/%s", apirev);
    }

    /**
     * Resource base URI with API revision number.
     */
    public static String getAuthUri() {
        return String.format("rest/auth/%s", apirev);
    }
    
    /**
     * Retrieves the given attachment record.
     *
     * @param id Internal JIRA ID of the attachment
     *
     * @return an attachment instance
     *
     * @throws JiraException when the retrieval fails
     */
    public Attachment getAttachement(String attachmentId) throws JiraException {
    	try {
        	return new Attachment(restclient.get(getBaseUri(), Attachment.URI, attachmentId));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve attachment " + attachmentId, ex);
        }
    }
    
    /**
     * 
     * @param issue
     * @return
     * @throws JiraException
     */
    public ArrayList<IssueHistory> getIssueChangeLog(Issue issue) throws JiraException {
        try {
            ArrayList<IssueHistory> changes = null;
            Map<String, Object> response = getNextPortion(issue, 0);

            while (true) {
            	Map<String, Object> opers = Field.getMap(response.get("changelog"));
                Integer totalObj = Field.getInteger(opers.get("total"));
                List<Object> histories = Field.getList(opers.get("histories"));

                if (changes == null) {
                    changes = new ArrayList<IssueHistory>(totalObj);
                }

                for (Object object : histories) {
                    changes.add(new IssueHistory(Field.getMap(object)));
                }

                if (changes.size() >= totalObj) {
                    break;
                } else {
                    response = getNextPortion(issue, changes.size());
                }
            } 
           
            return changes;
        } catch (Exception ex) {
            throw new JiraException(ex.getMessage(), ex);
        }
    }

    /**
     * Retrieves the given comment record.
     *
     * @param issue Internal JIRA ID of the associated issue
     * @param id Internal JIRA ID of the comment
     *
     * @return a comment instance
     *
     * @throws JiraException when the retrieval fails
     */
    public Comment getComment(String issueKey, String commentId) throws JiraException {
        try {
        	return new Comment(restclient.get(getBaseUri(), Issue.URI, issueKey, Comment.URI, commentId));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve comment " + commentId + " on issue " + issueKey, ex);
        }
    }

    /**
     * Retrieves the given component record.
     *
     * @param id Internal JIRA ID of the component
     *
     * @return a component instance
     *
     * @throws JiraException when the retrieval fails
     */
    public Component getComponent(String componentId) throws JiraException {
        try {
        	return new Component(restclient.get(getBaseUri(), Component.URI, componentId));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve component " + componentId, ex);
        }
    }
    
    /**
     * Retrieves the given custom field option record.
     *
     * @param id Internal JIRA ID of the custom field option
     *
     * @return a custom field option instance
     *
     * @throws JiraException when the retrieval fails
     */
    public CustomFieldOption getCustomFieldOption(String optionId) throws JiraException {
        try {
        	return new CustomFieldOption(restclient.get(getBaseUri(), CustomFieldOption.URI, optionId));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve custom field option " + optionId, ex);
        }
    }
    
    /**
     * Retrieves the given issue link record.
     *
     * @param id Internal JIRA ID of the issue link
     *
     * @return a issue link instance
     *
     * @throws JiraException when the retrieval fails
     */
    public IssueLink getIssueLink(String linkId) throws JiraException {
        try {
        	return new IssueLink(restclient.get(getBaseUri(), IssueLink.URI, linkId));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve issue link " + linkId, ex);
        }
    }
    
    /**
     * Retrieves the given issue type record.
     *
     * @param id Internal JIRA ID of the issue type
     *
     * @return an issue type instance
     *
     * @throws JiraException when the retrieval fails
     */
    public IssueType getIssueType(String typeId) throws JiraException {
        try {
        	return new IssueType(restclient.get(getBaseUri(), IssueType.URI, typeId));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve issue type " + typeId, ex);
        }
    }
    
    /**
     * Obtains the list of all issue types in Jira.
     * @return all issue types
     * @throws JiraException failed to obtain the issue type list.
     */
    public List<IssueType> getIssueTypes() throws JiraException {
        try {
            return Field.getResourceArray(IssueType.class, Field.getList(restclient.get(getBaseUri(), IssueType.URI)));
        } catch (Exception ex) {
            throw new JiraException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Retrieves the given issue link type record.
     *
     * @param id Internal JIRA ID of the issue link type
     *
     * @return a issue link type instance
     *
     * @throws JiraException when the retrieval fails
     */
    public LinkType getLinkType(String typeId) throws JiraException {
        try {
        	return new LinkType(restclient.get(getBaseUri(), LinkType.URI, typeId));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve issue link type " + typeId, ex);
        }
    }
    
    /**
     * Retrieves the given priority record.
     *
     * @param id Internal JIRA ID of the priority
     *
     * @return a priority instance
     *
     * @throws JiraException when the retrieval fails
     */
    public Priority getPriority(String priorityId) throws JiraException {
        try {
        	return new Priority(restclient.get(getBaseUri(), Priority.URI, priorityId));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve priority " + priorityId, ex);
        }
    }
    
    /**
    *
    * @return a list of all priorities available in the Jira installation
    * @throws JiraException
    */
   public List<Priority> getPriorities() throws JiraException {
       try {
    	   return Field.getResourceArray(Priority.class, Field.getList(restclient.get(getBaseUri(), Priority.URI)));
       } catch (Exception ex) {
           throw new JiraException(ex.getMessage(), ex);
       }
   }
    
    /**
     * Retrieves the given project record.
     *
     * @param key Project key
     *
     * @return a project instance
     *
     * @throws JiraException when the retrieval fails
     */
    public Project getProject(String projectKey) throws JiraException {
        try {
            return new Project(restclient.get(getBaseUri(), Project.URI, projectKey));
        } catch (Exception ex) {
            throw new JiraException(ex.getMessage(), ex);
        }
    }
    
    /**
     * Retrieves all project records visible to the session user.
     *
     * @return a list of projects; not all data is returned for each project; to get
     * the extra data use {@link #getProject(String)}
     *
     * @throws JiraException when the retrieval fails
     */
    public List<Project> getProjects() throws JiraException {
        try {
        	return Field.getResourceArray(Project.class, 
        			Field.getList(restclient.get(getBaseUri(), Project.URI)));
        } catch (Exception ex) {
            throw new JiraException(ex.getMessage(), ex);
        }
    }
    
    public List<RemoteLink> getRemoteLinks(String issueKey) throws JiraException {
        try {
        	return Field.getResourceArray(RemoteLink.class, 
        			Field.getList(restclient.get(getBaseUri(), Issue.URI, issueKey, RemoteLink.URI)));
        } catch (Exception ex) {
            throw new JiraException("Failed to get remote links for issue " + issueKey, ex);
        }
    }
    
    /**
     * Retrieves the given resolution record.
     *
     * @param id Internal JIRA ID of the resolution
     *
     * @return a resolution instance
     *
     * @throws JiraException when the retrieval fails
     */
    public Resolution getResolution(String resolutionId) throws JiraException {
        try {
        	return new Resolution(restclient.get(getBaseUri(), Resolution.URI, resolutionId));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve resolution " + resolutionId, ex);
        }
    }
    
    /**
     * Retrieves the given status record.
     *
     * @param id Internal JIRA ID of the status
     *
     * @return a status instance
     *
     * @throws JiraException when the retrieval fails
     */
    public Status getStatus(String statusId) throws JiraException {
        try {
        	return new Status(restclient.get(getBaseUri(), Status.URI, statusId));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve status " + statusId, ex);
        }
    }
    
    /**
     * Retrieves the given user record.
     *
     * @param username   User logon name
     * @return a user instance
     * @throws JiraException when the retrieval fails
     */
    public User getUser(String username) throws JiraException {
        try {
        	Map<String, String> params = new HashMap<String, String>();
            params.put("username", username);
            return new User(restclient.get(params, getBaseUri(), User.URI));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve user " + username, ex);
        }
    }
    
    /**
     * Retrieves the given version record.
     *
     * @param id         Internal JIRA ID of the version
     * @return a version instance
     * @throws JiraException when the retrieval fails
     */
    public Version getVersion(String versionId) throws JiraException {
        try {
        	return new Version(restclient.get(getBaseUri(), Version.URI, versionId));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve version " + versionId, ex);
        }
    }

    /**
     * Retrieves the given watches record.
     *
     * @param issue Internal JIRA ID of the issue
     *
     * @return a watches instance
     *
     * @throws JiraException when the retrieval fails
     */
    public Watches getWatches(String issueKey) throws JiraException {
        try {
        	return new Watches(restclient.get(getBaseUri(), Issue.URI, issueKey, Watches.URI));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve watches for issue " + issueKey, ex);
        }
    }
    
    /**
     * Retrieves the given work log record.
     *
     * @param issue Internal JIRA ID of the associated issue
     * @param id Internal JIRA ID of the work log
     *
     * @return a work log instance
     *
     * @throws JiraException when the retrieval fails
     */
    public WorkLog getWorkLog(String issueKey, String worklogId) throws JiraException {
        try {
        	return new WorkLog(restclient.get(getBaseUri(), Issue.URI, issueKey, WorkLog.URI, worklogId));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve work log " + worklogId + " on issue " + issueKey, ex);
        }
    }
    
    public List<WorkLog> getAllWorkLogs(String issueKey) throws JiraException {
        try {
        	return Field.getResourceArray(WorkLog.class,  
        		Field.getList(restclient.get(getBaseUri(), Issue.URI, issueKey, WorkLog.URI)));
        } catch (Exception ex) {
            throw new JiraException("Failed to get worklog for issue " + issueKey, ex);
        }
    }
    
    /**
     * Retrieves the given votes record.
     *
     * @param issue Internal JIRA ID of the issue
     *
     * @return a votes instance
     *
     * @throws JiraException when the retrieval fails
     */
    public Votes getVotes(String issueKey) throws JiraException {
        try {
        	return new Votes(restclient.get(getBaseUri(), Issue.URI, issueKey, Votes.URI));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve votes for issue " + issueKey, ex);
        }
    }
    
    /**
     * Merges the given version with current version
     * 
     * @param version
     *            The version to merge
     */
    public void mergeVersionWith(Version version, String targetVersionId) {
        JSONObject req = new JSONObject();
        req.put("description", version.getDescription());
        req.put("name", version.getName());
        req.put("archived", version.isArchived());
        req.put("released", version.isReleased());
        req.put("releaseDate", version.getReleaseDate());

        try {
            restclient.put(req, getBaseUri(), Version.URI, targetVersionId);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to merge", ex);
        }
    }

    /**
    * Copies the version to the given project
    * 
    * @param project
    *            The project the version will be copied to
    */
    public void copyVersionTo(Project project, Version version) {
        JSONObject req = new JSONObject();
        req.put("description", version.getDescription());
        req.put("name", version.getName());
        req.put("archived", version.isArchived());
        req.put("released", version.isReleased());
        req.put("releaseDate", version.getReleaseDate());
        req.put("project", project.getKey());
        req.put("projectId", project.getId());
      
        try {
            restclient.post(req, getBaseUri(), Version.URI);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to copy to project '" + project.getKey() + "'", ex);
        }
    }
    
    /**
     * Deletes a component from a project.
     * 
     * @throws JiraException failed to delete the component
     */
    public void deleteComponent(String componentId) throws JiraException {
        try {
            restclient.delete(getBaseUri(), Component.URI, componentId);
        } catch (Exception ex) {
            throw new JiraException("Failed to delete component " + componentId, ex);
        }
    }
    
    /**
     * Deletes this issue link record.
     *
     * @throws JiraException when the delete fails
     */
    public void deleteIssueLink(String linkId) throws JiraException {
        try {
            restclient.delete(getBaseUri(), IssueLink.URI, linkId);
        } catch (Exception ex) {
            throw new JiraException("Failed to delete issue link " + linkId, ex);
        }
    }
    
    /**
     * Downloads attachment to byte array
     *
     * @return a byte[]
     *
     * @throws JiraException when the download fails
     */
    public byte[] downloadAttachment(String contentUrl) throws JiraException {
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	
    	try{
        	HttpGet get = new HttpGet(contentUrl);
        	HttpResponse response = restclient.getHttpClient().execute(get);
        	HttpEntity entity = response.getEntity();
        	if (entity != null) {
        	    InputStream inputStream = entity.getContent();
        	    int next = inputStream.read();
        	    while (next > -1) {
        	        bos.write(next);
        	        next = inputStream.read();
        	    }
        	    bos.flush();
        	}
    	} catch(IOException e) {
    		  throw new JiraException(String.format("Failed downloading attachment from %s: %s", contentUrl, e.getMessage()));
    	}
    	return bos.toByteArray();
    }
    
    /**
     * count issues with the given query.
     *
     * @param jql JQL statement
     *
     * @return the count
     *
     * @throws JiraException when the search fails
     */
    public int countIssues(String jql) throws JiraException {
    	Map<String, Object> data = null;
        
        try {
            Map<String, String> queryParams = new HashMap<String, String>();
            queryParams.put("jql", jql);
            queryParams.put("maxResults", "1");
            data = restclient.get(queryParams, getBaseUri(), SEARCH_URI);
        } catch (Exception ex) {
            throw new JiraException("Failed to search issues", ex);
        }

        return Field.getInteger(data.get("total"));
    }
        
    public Map<String, Object> getCreateMetadata(String projectKey, String issueType) throws JiraException {
    	Map<String, Object> data = null;

        try {
        	Map<String, String> queryParams = new HashMap<String, String>();
        	queryParams.put("expand", "projects.issuetypes.fields");
        	queryParams.put("projectKeys", projectKey);
        	queryParams.put("issuetypeNames", issueType);
        	data = restclient.get(queryParams, getBaseUri(), Issue.URI, CREATEMETA_URI);
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve issue metadata", ex);
        }

        if (!(data.get("projects") instanceof List))
            throw new JiraException("Create metadata is malformed");

        List<Project> projects = Field.getResourceArray(Project.class, data.get("projects"));

        if (projects.isEmpty() || projects.get(0).getIssueTypes().isEmpty())
            throw new JiraException("Project '" + projectKey + "'  or issue type '" + issueType + 
                    "' missing from create metadata. Do you have enough permissions?");

        return projects.get(0).getIssueTypes().get(0).getFields();
    }

    public Map<String, Object> getEditMetadata(String issueKey) throws JiraException {
    	Map<String, Object> data = null;

        try {
        	data = restclient.get(getBaseUri(), Issue.URI, issueKey, EDITMETA_URI);
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve issue metadata", ex);
        }

        if (!(data.get("fields") instanceof Map))
            throw new JiraException("Edit metadata is malformed");

        return Field.getMap(data.get("fields"));
    }

    public List<Transition> getTransitions(String issueKey) throws JiraException {
        Map<String, Object> data = null;

        try {
        	Map<String, String> queryParams = new HashMap<String, String>();
        	queryParams.put("expand", "transitions.fields");
        	data = restclient.get(queryParams, getBaseUri(), Issue.URI, issueKey, Transition.URI);
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve transitions", ex);
        }

        if (!(data.get("transitions") instanceof List))
            throw new JiraException("Transition metadata is missing.");

        return Field.getResourceArray(Transition.class, data.get("transitions"));
    }

    /**
     * Adds an attachment to this issue.
     *
     * @param file java.io.File
     *
     * @throws JiraException when the attachment creation fails
     */
    public void addAttachment(String issueKey, File file) throws JiraException {
        try {
            restclient.post(file, getBaseUri(), Issue.URI, issueKey, Attachment.URI);
        } catch (Exception ex) {
            throw new JiraException("Failed add attachment to issue " + issueKey, ex);
        }
    }
    
    /**
     * Adds attachments to this issue.
     *
     * @param attachments  the attachments to add
     *
     * @throws JiraException when the attachments creation fails
     */
    public void addAttachments(String issueKey, NewAttachment... attachments) throws JiraException {
        if (attachments == null) {
            throw new NullPointerException("attachments may not be null");
        }
        if (attachments.length == 0) {
            return;
        }
        
        try {
            restclient.post(restclient.buildURI(getBaseUri(), Issue.URI, issueKey, Attachment.URI_MULTI), attachments);
        } catch (Exception ex) {
            throw new JiraException("Failed add attachments to issue " + issueKey, ex);
        }
    }
    
    /**
     * Adds a comment to this issue.
     *
     * @param body Comment text
     *
     * @throws JiraException when the comment creation fails
     */
    public void addComment(String issueKey, String body) throws JiraException {
        addComment(issueKey, body, null, null);
    }

    /**
     * Adds a comment to this issue with limited visibility.
     *
     * @param body Comment text
     * @param visType Target audience type (role or group)
     * @param visName Name of the role or group to limit visibility to
     *
     * @throws JiraException when the comment creation fails
     */
    public void addComment(String issueKey, String body, String visType, String visName) throws JiraException {
        JSONObject req = new JSONObject();
        req.put("body", body);

        if (visType != null && visName != null) {
            JSONObject vis = new JSONObject();
            vis.put("type", visType);
            vis.put("value", visName);

            req.put("visibility", vis);
        }

        try {
            restclient.post(req, getBaseUri(), Issue.URI, issueKey, Comment.URI);
        } catch (Exception ex) {
            throw new JiraException("Failed add comment to issue " + issueKey, ex);
        }
    }

    /**
     * Adds a watcher to the issue.
     *
     * @param username Username of the watcher to add
     *
     * @throws JiraException when the operation fails
     */
    public void addWatcher(String issueKey, String username) throws JiraException {
        try {
            restclient.post(restclient.buildURI(getBaseUri(), Issue.URI, issueKey, "watchers"), username);
        } catch (Exception ex) {
            throw new JiraException("Failed to add watcher (" + username + ") to issue " + issueKey, ex);
        }
    }

    /**
     * Removes a watcher to the issue.
     *
     * @param username Username of the watcher to remove
     *
     * @throws JiraException when the operation fails
     */
    public void deleteWatcher(String issueKey, String username) throws JiraException {
        try {
        	Map<String, String> queryParams = new HashMap<String, String>();
        	queryParams.put("username", username);
            restclient.delete(queryParams, getBaseUri(), Issue.URI, issueKey, "watchers");
        } catch (Exception ex) {
            throw new JiraException("Failed to remove watch (" + username + ") from issue " + issueKey, ex);
        }
    }
    
    /**
     * Links this issue with another issue and adds a comment with limited visibility.
     *
     * @param issue Other issue key
     * @param type Link type name
     * @param body Comment text
     * @param visType Target audience type (role or group)
     * @param visName Name of the role or group to limit visibility to
     *
     * @throws JiraException when the link creation fails
     */
    public void linkIssue(String inwardIssue, String outwardIssue, String type, String body, String visType, String visName) throws JiraException {
        JSONObject req = new JSONObject();

        JSONObject t = new JSONObject();
        t.put("name", type);
        req.put("type", t);

        JSONObject inward = new JSONObject();
        inward.put("key", inwardIssue);
        req.put("inwardIssue", inward);

        JSONObject outward = new JSONObject();
        outward.put("key", outwardIssue);
        req.put("outwardIssue", outward);

        if (body != null) {
            JSONObject comment = new JSONObject();
            comment.put("body", body);

            if (visType != null && visName != null) {
                JSONObject vis = new JSONObject();
                vis.put("type", visType);
                vis.put("value", visName);

                comment.put("visibility", vis);
            }

            req.put("comment", comment);
        }

        try {
            restclient.post(req, getBaseUri(), IssueLink.URI);
        } catch (Exception ex) {
            throw new JiraException("Failed to link issue " + inwardIssue + " with issue " + outwardIssue, ex);
        }
    }
    
    /**
     * Links this issue with another issue.
     *
     * @param issue Other issue key
     * @param type Link type name
     *
     * @throws JiraException when the link creation fails
     */
    public void linkIssue(String inwardIssue, String outwardIssue, String type) throws JiraException {
    	linkIssue(inwardIssue, outwardIssue, type, null, null, null);
    }

    /**
     * Links this issue with another issue and adds a comment.
     *
     * @param issue Other issue key
     * @param type Link type name
     * @param body Comment text
     *
     * @throws JiraException when the link creation fails
     */
    public void linkIssue(String inwardIssue, String outwardIssue, String type, String body) throws JiraException {
    	linkIssue(inwardIssue, outwardIssue, type, body, null, null);
    }

    /**
     * Adds a remote link to this issue.
     *
     * @param url Url of the remote link
     * @param title Title of the remote link
     * @param summary Summary of the remote link
     *
     * @throws JiraException when the link creation fails
     */
    public void addRemoteLink(String issueKey, String url, String title, String summary) throws JiraException {
        JSONObject req = new JSONObject();
        JSONObject obj = new JSONObject();
        
        obj.put("url", url);
        obj.put("title", title);
        obj.put("summary", summary);
        
        req.put("object", obj);

        try {
            restclient.post(req, getBaseUri(), Issue.URI, issueKey, RemoteLink.URI);
        } catch (Exception ex) {
            throw new JiraException("Failed add remote link to issue " + issueKey, ex);
        }
    }
    
    /**
     * Creates a new JIRA issue.
     *
     * @param project Key of the project to create the issue in
     * @param issueType Name of the issue type to create
     *
     * @return a fluent create instance
     *
     * @throws JiraException when the client fails to retrieve issue metadata
     */
    public FluentCreateIssue createIssue(String project, String issueType) throws JiraException {
    	FluentCreateIssue fc = new FluentCreateIssue(restclient, getCreateMetadata(project, issueType));

        fc.field(Field.PROJECT, project);
        fc.field(Field.ISSUE_TYPE, issueType);
        
        return fc;
    }
    
    /**
     * Creates a new sub-task.
     *
     * @return a fluent create instance
     *
     * @throws JiraException when the client fails to retrieve issue metadata
     */
    public FluentCreateIssue createSubtask(String issueParent, String issueType) throws JiraException {
    	Issue issue = getIssue(issueParent, null, null);
    	FluentCreateIssue fc = createIssue(issue.getProject().getKey(), issueType);
    	fc.field(Field.PARENT, issueParent);
    	
    	return fc;
    }
    
    /**
     * Creates a new sub-task.
     *
     * @return a fluent create instance
     *
     * @throws JiraException when the client fails to retrieve issue metadata
     */
    public FluentCreateIssue createSubtask(String issueParent) throws JiraException {
    	return createSubtask(issueParent, "Sub-task");
    }

    public Issue getIssue(String issueKey, String includedFields, String expand) throws JiraException {
        try {
        	Map<String, String> queryParams = new HashMap<String, String>();
        	if (includedFields != null) {
        		queryParams.put("fields", includedFields);
            }
            if (expand != null) {
            	queryParams.put("expand", expand);
            }
        	return new Issue(restclient.get(queryParams, getBaseUri(), Issue.URI, issueKey));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve issue " + issueKey, ex);
        }
    }

    /**
     * Search for issues with the given query and specify which fields to
     * retrieve. If the total results is bigger than the maximum returned
     * results, then further calls can be made using different values for
     * the <code>startAt</code> field to obtain all the results.
     *
     * @param jql JQL statement
     *
     * @param includedFields Specifies which issue fields will be included in
     * the result.
     * <br>Some examples how this parameter works:
     * <ul>
     * <li>*all - include all fields</li>
     * <li>*navigable - include just navigable fields</li>
     * <li>summary,comment - include just the summary and comments</li>
     * <li>*all,-comment - include all fields</li>
     * </ul>
     * 
     * @param maxResults if non-<code>null</code>, defines the maximum number of
     * results that can be returned 
     * 
     * @param startAt if non-<code>null</code>, defines the first issue to
     * return
     *
     * @param expandFields fields to expand when obtaining the issue
     *
     * @return a search result structure with results
     *
     * @throws JiraException when the search fails
     */
    public SearchResult searchIssues(String jql, String includedFields, String expandFields, Integer maxResults, Integer startAt) 
    		throws JiraException {

    	Map<String, Object> data = null;

        try {
            Map<String, String> queryParams = new HashMap<String, String>();
            queryParams.put("jql", jql);
            if (maxResults != null) {
                queryParams.put("maxResults", String.valueOf(maxResults));
            }
            if (includedFields != null) {
                queryParams.put("fields", includedFields);
            }
            if (expandFields != null) {
                queryParams.put("expand", expandFields);
            }
            if (startAt != null) {
                queryParams.put("startAt", String.valueOf(startAt));
            }

            data = restclient.get(queryParams, getBaseUri(), SEARCH_URI);
        } catch (Exception ex) {
            throw new JiraException("Failed to search issues", ex);
        }

        SearchResult sr = new SearchResult();

        sr.start = Field.getInteger(data.get("startAt"));
        sr.max = Field.getInteger(data.get("maxResults"));
        sr.total = Field.getInteger(data.get("total"));
        sr.issues = Field.getResourceArray(Issue.class, data.get("issues"));
        
        return sr;
    }
    
    /**
     * Begins a transition field chain.
     *
     * @return a fluent transition instance
     *
     * @throws JiraException when the client fails to retrieve issue metadata
     */
    public FluentTransitionIssue transition(String issueKey) throws JiraException {
        return new FluentTransitionIssue(restclient, getTransitions(issueKey), issueKey);
    }

    /**
     * Begins an update field chain.
     *
     * @return a fluent update instance
     *
     * @throws JiraException when the client fails to retrieve issue metadata
     */
    public FluentUpdateIssue update(String issueKey) throws JiraException {
        return new FluentUpdateIssue(restclient, getEditMetadata(issueKey), issueKey);
    }

    /**
     * Casts a vote in favour of an issue.
     *
     * @throws JiraException when the voting fails
     */
    public void vote(String issueKey) throws JiraException {
        try {
            restclient.post(getBaseUri(), Issue.URI, issueKey, Votes.URI);
        } catch (Exception ex) {
            throw new JiraException("Failed to vote on issue " + issueKey, ex);
        }
    }

    /**
     * Removes the current user's vote from the issue.
     *
     * @throws JiraException when the voting fails
     */
    public void unvote(String issueKey) throws JiraException {
        try {
            restclient.delete(getBaseUri(), Issue.URI, issueKey, Votes.URI);
        } catch (Exception ex) {
            throw new JiraException("Failed to unvote on issue " + issueKey, ex);
        }
    }


    /**
     * Get a list of options for a custom field
     *
     * @param field field id
     * @param project Key of the project context
     * @param issueType Name of the issue type
     *
     * @return a search result structure with results
     *
     * @throws JiraException when the search fails
     */
    public List<CustomFieldOption> getCustomFieldAllowedValues(String field, String project, String issueType) throws JiraException {
        Map<String, Object> createMetadata = getCreateMetadata(project, issueType);
        if (!(createMetadata.get(field) instanceof Map)) {
        	throw new JiraException("Field not found.");
        }
        Map<String, Object> fieldMetadata = Field.getMap(createMetadata.get(field));
        if (fieldMetadata == null) {
        	throw new JiraException("Field meta data not found.");
        }
        return Field.getResourceArray(CustomFieldOption.class, fieldMetadata.get("allowedValues"));
    }

    /**
     * Get a list of options for a components
     *
     * @param project Key of the project context
     * @param issueType Name of the issue type
     *
     * @return a search result structure with results
     *
     * @throws JiraException when the search fails
     */
    public List<Component> getComponentsAllowedValues(String project, String issueType) throws JiraException {
    	Map<String, Object> createMetadata = getCreateMetadata(project, issueType);
    	if (!(createMetadata.get(Field.COMPONENTS) instanceof Map)) {
    		throw new JiraException("Field not found.");
    	}
    	Map<String, Object> fieldMetadata = Field.getMap(createMetadata.get(Field.COMPONENTS));
    	if (fieldMetadata == null) {
    		throw new JiraException("Field meta data not found.");
    	}
        return Field.getResourceArray(Component.class, fieldMetadata.get("allowedValues"));
    }

    public String getSelf() {
        return username;
    }
    
    /**
     * Creates a new component in the given project.
     *
     * @param project Key of the project to create in
     *
     * @return a fluent create instance
     */
    public FluentCreateComponent createComponent(String project) {
    	FluentCreateComponent fc = new FluentCreateComponent(restclient, project);
        return fc;
    }
    
    public ArrayList<IssueHistory> filterChangeLog(List<IssueHistory> histoy, String fields) throws JiraException {
        ArrayList<IssueHistory> result = new ArrayList<IssueHistory>(histoy.size());
        fields = "," + fields + ",";

        for (IssueHistory record : histoy) {
            ArrayList<IssueHistoryItem> list = new ArrayList<IssueHistoryItem>(record.getChanges().size());
            for (IssueHistoryItem item : record.getChanges()) {
                if (fields.contains(item.getField())) {
                    list.add(item);
                }
            }

            if (list.size() > 0) {
                result.add(new IssueHistory(record, list));
            }
        }
        return result;
    }

    private Map<String, Object> getNextPortion(Issue issue, Integer startAt) 
    		throws URISyntaxException, RestException, IOException, AuthenticationException, JiraException {
        
    	Map<String, String> queryParams = new HashMap<String, String>();
        if (startAt != null) {
        	queryParams.put("startAt", String.valueOf(startAt));
        }
        queryParams.put("expand", "changelog.fields");
        
        return restclient.get(queryParams, getBaseUri(), Issue.URI, issue.id);
    }
}
