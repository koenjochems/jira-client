package net.rcarz.jiraclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.junit.Test;

public class VersionTest {

    @Test
    public void testVersionInit() throws JiraException{
        new Version(null);
    }

    @Test
    public void testVersionJSON() throws JiraException{
        Version version = new Version(getTestData());

        assertEquals(version.getId(),"10200");
        assertEquals(version.getName(),"1.0");
        assertFalse(version.isArchived());
        assertFalse(version.isReleased());
        assertEquals(version.getReleaseDate(),"2013-12-01");
        assertEquals(version.getDescription(),"First Full Functional Build");
    }

    @Test
    public void testVersionToString() throws JiraException{
        Version version = new Version(getTestData());
        assertEquals(version.toString(),"1.0");
    }

    private Map<String, Object> getTestData() throws JSONException, JiraException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id","10200");
        jsonObject.put("description","First Full Functional Build");
        jsonObject.put("name","1.0");
        jsonObject.put("archived",false);
        jsonObject.put("released",false);
        jsonObject.put("releaseDate","2013-12-01");
        return RestClient.JSONtoMap(jsonObject);
    }
}

/**
 "fixVersions": [
 {
 "self": "https://brainbubble.atlassian.net/rest/api/2/version/10200",
 "id": "10200",
 "description": "First Full Functional Build",
 "name": "1.0",
 "archived": false,
 "released": false,
 "releaseDate": "2013-12-01"
 }
 ],
 **/
