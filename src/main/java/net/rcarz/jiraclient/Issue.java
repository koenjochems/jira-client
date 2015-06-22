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

import java.io.File;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Represents a JIRA issue.
 */
public class Issue extends Resource {

    public static final class NewAttachment {

        private final String filename;
        private final Object content;

        public NewAttachment(File content) {
            this(content.getName(), content);
        }

        public NewAttachment(String filename, File content) {
            this.filename = requireFilename(filename);
            this.content = requireContent(content);
        }

        public NewAttachment(String filename, InputStream content) {
            this.filename = requireFilename(filename);
            this.content = requireContent(content);
        }

        public NewAttachment(String filename, byte[] content) {
            this.filename = requireFilename(filename);
            this.content = requireContent(content);
        }

        String getFilename() {
            return filename;
        }

        Object getContent() {
            return content;
        }

        private static String requireFilename(String filename) {
            if (filename == null) {
                throw new NullPointerException("filename may not be null");
            }
            if (filename.length() == 0) {
                throw new IllegalArgumentException("filename may not be empty");
            }
            return filename;
        }

        private static Object requireContent(Object content) {
            if (content == null) {
                throw new NullPointerException("content may not be null");
            }
            return content;
        }
    }

    public static final String URI = "issue";
    
    private Map<String, Object> fields = null;

    /* system fields */
    private User assignee = null;
    private List<Attachment> attachments = null;
    private ChangeLog changeLog = null;
    private List<Comment> comments = null;
    private List<Component> components = null;
    private String description = null;
    private Date dueDate = null;
    private List<Version> fixVersions = null;
    private List<IssueLink> issueLinks = null;
    private IssueType issueType = null;
    private List<String> labels = null;
    private Issue parent = null;
    private Priority priority = null;
    private Project project = null;
    private User reporter = null;
    private Resolution resolution = null;
    private Date resolutionDate = null;
    private Status status = null;
    private List<Issue> subtasks = null;
    private String summary = null;
    private TimeTracking timeTracking = null;
    private List<Version> versions = null;
    private Votes votes = null;
    private Watches watches = null;
    private List<WorkLog> workLogs = null;
    private Integer timeEstimate = null;
    private Integer timeSpent = null;
    private Date createdDate = null;
    private Date updatedDate = null;

    /**
     * Creates an issue from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected Issue(Map<String, Object> data) throws JiraException {
        super(data);

        if (data != null) {
        	deserialise(data);
        }
    }
    
    /**
     * @param data Map of the JSON payload
     */
    @Override
	protected void deserialise(Map<String, Object> data) throws JiraException {
    	self = Field.getString(data.get("self"));

        fields = Field.getMap(data.get("fields"));

        if (fields != null) {
	        assignee = Field.getResource(User.class, fields.get(Field.ASSIGNEE));
	        attachments = Field.getResourceArray(Attachment.class, fields.get(Field.ATTACHMENT));
	        changeLog = Field.getResource(ChangeLog.class, data.get(Field.CHANGE_LOG));
	        comments = Field.getResourceArray(Comment.class, fields.get(Field.COMMENT));
	        components = Field.getResourceArray(Component.class, fields.get(Field.COMPONENTS));
	        description = Field.getString(fields.get(Field.DESCRIPTION));
	        dueDate = Field.getDate(fields.get(Field.DUE_DATE));
	        fixVersions = Field.getResourceArray(Version.class, fields.get(Field.FIX_VERSIONS));
	        issueLinks = Field.getResourceArray(IssueLink.class, fields.get(Field.ISSUE_LINKS));
	        issueType = Field.getResource(IssueType.class, fields.get(Field.ISSUE_TYPE));
	        labels = Field.getStringArray(fields.get(Field.LABELS));
	        parent = Field.getResource(Issue.class, fields.get(Field.PARENT));
	        priority = Field.getResource(Priority.class, fields.get(Field.PRIORITY));
	        project = Field.getResource(Project.class, fields.get(Field.PROJECT));
	        reporter = Field.getResource(User.class, fields.get(Field.REPORTER));
	        resolution = Field.getResource(Resolution.class, fields.get(Field.RESOLUTION));
	        resolutionDate = Field.getDateTime(fields.get(Field.RESOLUTION_DATE));
	        status = Field.getResource(Status.class, fields.get(Field.STATUS));
	        subtasks = Field.getResourceArray(Issue.class, fields.get(Field.SUBTASKS));
	        summary = Field.getString(fields.get(Field.SUMMARY));
	        timeTracking = Field.getTimeTracking(fields.get(Field.TIME_TRACKING));
	        versions = Field.getResourceArray(Version.class, fields.get(Field.VERSIONS));
	        votes = Field.getResource(Votes.class, fields.get(Field.VOTES));
	        watches = Field.getResource(Watches.class, fields.get(Field.WATCHES));
	        workLogs = Field.getResourceArray(WorkLog.class, fields.get(Field.WORKLOG));
	        timeEstimate = Field.getInteger(fields.get(Field.TIME_ESTIMATE));
	        timeSpent = Field.getInteger(fields.get(Field.TIME_SPENT));
	        createdDate = Field.getDateTime(fields.get(Field.CREATED_DATE));
	        updatedDate = Field.getDateTime(fields.get(Field.UPDATED_DATE));
        }
	}

    /**
     * Gets an arbitrary field by its name.
     *
     * @param name Name of the field to retrieve
     *
     * @return the field value or null if not found
     */
    public Object getField(String name) {
        return fields != null ? fields.get(name) : null;
    }

    @Override
    public String toString() {
        return getKey();
    }

    public ChangeLog getChangeLog() {
        return changeLog;
    }

    public String getKey() {
        return key;
    }

    public User getAssignee() {
        return assignee;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<Component> getComponents() {
        return components;
    }

    public String getDescription() {
        return description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public List<Version> getFixVersions() {
        return fixVersions;
    }

    public List<IssueLink> getIssueLinks() {
        return issueLinks;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public List<String> getLabels() {
        return labels;
    }

    public Issue getParent() {
        return parent;
    }

    public Priority getPriority() {
        return priority;
    }

    public Project getProject() {
        return project;
    }

    public User getReporter() {
        return reporter;
    }

    public Resolution getResolution() {
        return resolution;
    }

    public Date getResolutionDate() {
        return resolutionDate;
    }

    public Status getStatus() {
        return status;
    }

    public List<Issue> getSubtasks() {
        return subtasks;
    }

    public String getSummary() {
        return summary;
    }

    public TimeTracking getTimeTracking() {
        return timeTracking;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public Votes getVotes() {
        return votes;
    }

    public Watches getWatches() {
        return watches;
    }

    public List<WorkLog> getWorkLogs() {
        return workLogs;
    }

    public Integer getTimeSpent() {
        return timeSpent;
    }

    public Integer getTimeEstimate() {
        return timeEstimate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

	@Override
	public String getValue() {
		return getKey();
	}
}
