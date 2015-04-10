package net.rcarz.jiraclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;

public class IssueTest {

    /**
     * If no exception thrown the test is passed.
     * @throws JiraException 
     */
    @Test
    public void testCreateIssue() throws JiraException {
        new Issue(Utils.getTestIssue());
    }

    @Test
    public void testGetIssueStatus() throws JiraException {
        String statusName = "To Do";
        String statusID = "10004";
        String description = "Issue is currently in progress.";
        String iconURL = "https://brainbubble.atlassian.net/images/icons/statuses/open.png";

        Issue issue = new Issue(Utils.getTestIssue());
        assertNotNull(issue.getStatus());
        assertEquals(description, issue.getStatus().getDescription());
        assertEquals(iconURL, issue.getStatus().getIconUrl());

        assertEquals(statusName, issue.getStatus().getName());
        assertEquals(statusID, issue.getStatus().getId());
    }

    @Test
    public void getReporter() throws JiraException {
        Issue issue = new Issue(Utils.getTestIssue());
        assertNotNull(issue.getReporter());

        User reporter = issue.getReporter();

        assertEquals(reporter.getDisplayName(), "Joseph McCarthy");
        assertEquals(reporter.getName(), "joseph");
        assertTrue(reporter.isActive());
        assertEquals(reporter.getEmail(), "joseph.b.mccarthy2012@googlemail.com");

        Map<String, Object> avatars = reporter.getAvatarUrls();

        assertNotNull(avatars);
        assertEquals(avatars.size(), 4);

        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=16", avatars.get("16x16"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=24", avatars.get("24x24"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=32", avatars.get("32x32"));
        assertEquals("https://secure.gravatar.com/avatar/a5a271f9eee8bbb3795f41f290274f8c?d=mm&s=48", avatars.get("48x48"));
    }

    @Test
    public void testGetIssueType() throws JiraException{
        Issue issue = new Issue(Utils.getTestIssue());
        IssueType issueType = issue.getIssueType();
        assertNotNull(issueType);

        assertFalse(issueType.isSubtask());
        assertEquals(issueType.getName(), "Story");
        assertEquals(issueType.getId(), "7");
        assertEquals(issueType.getIconUrl(), "https://brainbubble.atlassian.net/images/icons/issuetypes/story.png");
        assertEquals(issueType.getDescription(), "This is a test issue type.");
    }

    @Test
    public void testGetVotes() throws JiraException{
        Issue issue = new Issue(Utils.getTestIssue());
        Votes votes = issue.getVotes();
        assertNotNull(votes);

        assertFalse(votes.hasVoted());
        assertEquals(votes.getVotes(),0);
    }

    @Test
    public void testGetWatchers() throws JiraException{
        Issue issue = new Issue(Utils.getTestIssue());
        Watches watches = issue.getWatches();

        assertNotNull(watches);

        assertFalse(watches.isWatching());
        assertEquals(watches.getWatchCount(), 0);
        assertEquals(watches.getSelf(), "https://brainbubble.atlassian.net/rest/api/2/issue/FILTA-43/watchers");
    }

    @Test
    public void testGetVersion() throws JiraException{
        Issue issue = new Issue(Utils.getTestIssue());
        List<Version> versions = issue.getFixVersions();

        assertNotNull(versions);
        assertEquals(versions.size(),1);

        Version version = versions.get(0);

        Assert.assertEquals(version.getId(), "10200");
        Assert.assertEquals(version.getName(), "1.0");
        assertFalse(version.isArchived());
        assertFalse(version.isReleased());
        Assert.assertEquals(version.getReleaseDate(), "2013-12-01");
        Assert.assertEquals(version.getDescription(), "First Full Functional Build");
    }

    @Test
    public void testPlainTimeTracking() throws JiraException {
        Issue issue = new Issue(Utils.getTestIssue());

        assertEquals(new Integer(144000), issue.getTimeEstimate());
        assertEquals(new Integer(86400), issue.getTimeSpent());
    }

    @Test
    public void testCreatedDate() throws JiraException{
        Issue issue = new Issue(Utils.getTestIssue());
        assertEquals(new DateTime(2013, 9, 29, 20, 16, 19, 854, DateTimeZone.forOffsetHours(1)).toDate(), issue.getCreatedDate());
    }

    @Test
    public void testUpdatedDate() throws JiraException{
      Issue issue = new Issue(Utils.getTestIssue());
      assertEquals(new DateTime(2013, 10, 9, 22, 24, 55, 961, DateTimeZone.forOffsetHours(1)).toDate(), issue.getUpdatedDate());
    }
}
