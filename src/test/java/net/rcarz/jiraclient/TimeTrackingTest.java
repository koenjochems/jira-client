package net.rcarz.jiraclient;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TimeTrackingTest {

    private Issue issue = null;
    private TimeTracking time = null;

    public TimeTrackingTest() throws JiraException {
    	issue = new Issue(Utils.getTestIssue());
        time = issue.getTimeTracking();
    }
    
    @Test
    public void testAttributeMappings() {
        assertEquals("1w", time.getOriginalEstimate());
        assertEquals(144000, time.getOriginalEstimateSeconds());

        assertEquals("2d", time.getRemainingEstimate());
        assertEquals(57600, time.getRemainingEstimateSeconds());

        assertEquals("3d", time.getTimeSpent());
        assertEquals(86400, time.getTimeSpentSeconds());
    }
}
