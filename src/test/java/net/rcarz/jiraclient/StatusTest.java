package net.rcarz.jiraclient;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.junit.Test;

public class StatusTest {

    private String statusID = "10004";
    private String description = "Issue is currently in progress.";
    private String iconURL = "https://site/images/icons/statuses/open.png";

    @Test
    public void testJSONDeserializer() throws IOException, URISyntaxException, JiraException {
        Status status = new Status(getTestData());
        assertEquals(status.getDescription(), description);
        assertEquals(status.getIconUrl(), iconURL);
        assertEquals(status.getName(), "Open");
        assertEquals(status.getId(), statusID);
    }

    private Map<String, Object> getTestData() throws JSONException, JiraException {
        JSONObject json = new JSONObject();
        json.put("description", description);
        json.put("name", "Open");
        json.put("iconUrl", iconURL);
        json.put("id", statusID);

        return RestClient.JSONtoMap(json);
    }

    @Test
    public void testStatusToString() throws URISyntaxException, JiraException {
        Status status = new Status(getTestData());
        assertEquals("Open",status.toString());
    }
}
