package net.rcarz.jiraclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.junit.Test;

public class IssueTypeTest {

    @Test
    public void testIssueTypeInit() throws JiraException {
        new IssueType(null);
    }

    @Test
    public void testGetIssueType() throws JiraException {
        IssueType issueType = new IssueType(getTestData());

        assertFalse(issueType.isSubtask());
        assertEquals(issueType.getName(), "Story");
        assertEquals(issueType.getId(), "7");
        assertEquals(issueType.getIconUrl(), "https://brainbubble.atlassian.net/images/icons/issuetypes/story.png");
        assertEquals(issueType.getDescription(), "This is a test issue type.");
    }

    @Test
    public void testIssueTypeToString() throws JiraException{
        IssueType issueType = new IssueType(getTestData());

        assertEquals(issueType.toString(),"Story");
    }

    private Map<String, Object> getTestData() throws JSONException, JiraException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("self", "https://brainbubble.atlassian.net/rest/api/2/issuetype/7");
        jsonObject.put("id", "7");
        jsonObject.put("description", "This is a test issue type.");
        jsonObject.put("iconUrl", "https://brainbubble.atlassian.net/images/icons/issuetypes/story.png");
        jsonObject.put("name", "Story");
        jsonObject.put("subtask", false);

        return RestClient.JSONtoMap(jsonObject);
    }
}
