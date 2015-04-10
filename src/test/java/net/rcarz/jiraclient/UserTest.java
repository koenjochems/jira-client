package net.rcarz.jiraclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.junit.Test;

public class UserTest {

    private java.lang.String username = "joseph";
    private java.lang.String displayName = "Joseph McCarthy";
    private java.lang.String email = "joseph.b.mccarthy2012@googlemail.com";
    private java.lang.String userID = "10";
    private boolean isActive = true;
    private String self = "https://brainbubble.atlassian.net/rest/api/2/user?username=joseph";

    @Test
    public void testJSONDeserializer() throws IOException, URISyntaxException, JiraException {
        User user = new User(getTestData());
        assertEquals(user.getName(), username);
        assertEquals(user.getDisplayName(), displayName);
        assertEquals(user.getEmail(), email);
        assertEquals(user.getId(), userID);

        Map<String, Object> avatars = user.getAvatarUrls();

        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=16", avatars.get("16x16"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=24", avatars.get("24x24"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=32", avatars.get("32x32"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=48", avatars.get("48x48"));

        assertTrue(user.isActive());
    }

    private Map<String, Object> getTestData() throws JSONException, JiraException {
        JSONObject json = new JSONObject();

        json.put("name", username);
        json.put("email", email);
        json.put("active", isActive);
        json.put("displayName", displayName);
        json.put("self", self);

        JSONObject images = new JSONObject();
        images.put("16x16", "https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=16");
        images.put("24x24", "https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=24");
        images.put("32x32", "https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=32");
        images.put("48x48", "https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=48");

        json.put("avatarUrls", images);
        json.put("id", "10");

        return RestClient.JSONtoMap(json);
    }

    @Test
    public void testStatusToString() throws URISyntaxException, JiraException {
        User user = new User(getTestData());
        assertEquals(username, user.toString());
    }
}
