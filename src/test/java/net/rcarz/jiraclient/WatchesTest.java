package net.rcarz.jiraclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.junit.Test;

public class WatchesTest {

    @Test
    public void testWatchesInit() throws JiraException {
        new Watches(null);
    }

    @Test
    public void testWatchesJSON() throws JiraException {
        Watches watches = new Watches(getTestData());

        assertFalse(watches.isWatching());
        assertEquals(watches.getWatchCount(), 0);
        assertEquals(watches.getId(), "10");
        assertEquals(watches.getSelf(), "https://brainbubble.atlassian.net/rest/api/2/issue/FILTA-43/watchers");
    }

    @Test
    public void testWatchesToString() throws JiraException {
        Watches watches = new Watches(getTestData());
        assertEquals(watches.toString(), "0");
    }

    private Map<String, Object> getTestData() throws JSONException, JiraException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", "10");
        jsonObject.put("self", "https://brainbubble.atlassian.net/rest/api/2/issue/FILTA-43/watchers");
        jsonObject.put("watchCount", 0);
        jsonObject.put("isWatching", false);
        
        return RestClient.JSONtoMap(jsonObject);
    }
}
