package net.rcarz.jiraclient;

import java.util.Map;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class VotesTest {

    @Test
    public void testVotesInit() throws JiraException{
        new Votes(null);
    }

    @Test
    public void testVotesJSON() throws JiraException{
        Votes votes = new Votes(getTestData());

        assertFalse(votes.hasVoted());
        assertEquals(votes.getId(),"10");
        assertEquals(votes.getVotes(),0);
        assertEquals(votes.getSelf(),"https://brainbubble.atlassian.net/rest/api/2/issue/FILTA-43/votes");
    }

    @Test
    public void testGetToString() throws JiraException{
        Votes votes = new Votes(getTestData());

        assertEquals(votes.toString(),"0");
    }

    private Map<String, Object> getTestData() throws JSONException, JiraException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("self","https://brainbubble.atlassian.net/rest/api/2/issue/FILTA-43/votes");
        jsonObject.put("votes",0);
        jsonObject.put("hasVoted",false);
        jsonObject.put("id","10");
        
        return RestClient.JSONtoMap(jsonObject);
    }
}


/**
 "votes": {
 "self": "https://brainbubble.atlassian.net/rest/api/2/issue/FILTA-43/votes",
 "votes": 0,
 "hasVoted": false
 },
 **/
