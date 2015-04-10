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

import java.util.List;
import java.util.Map;

/**
 * Represents a JIRA project.
 */
public class Project extends Resource {

	public static final String URI = "project";
	
    private Map<String, Object> avatarUrls = null;
    private String key = null;
    private String description = null;
    private User lead = null;
    private String assigneeType = null;
    private List<Component> components = null;
    private List<IssueType> issueTypes = null;
    private List<Version> versions = null;
    private Map<String, Object> roles = null;

    /**
     * Creates a project from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected Project(Map<String, Object> data) throws JiraException {
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
    	avatarUrls = Field.getMap(data.get("avatarUrls"));
        key = Field.getString(data.get("key"));
        description = Field.getString(data.get("description"));
        lead = Field.getResource(User.class, data.get("lead"));
        assigneeType = Field.getString(data.get("assigneeType"));
        components = Field.getResourceArray(Component.class, data.get("components"));
        issueTypes = Field.getResourceArray(IssueType.class, data.containsKey("issueTypes") ? data.get("issueTypes") : data.get("issuetypes"));
        versions = Field.getResourceArray(Version.class, data.get("versions"));
        roles = Field.getMap(data.get("roles"));
	}

    public Map<String, Object> getAvatarUrls() {
        return avatarUrls;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public User getLead() {
        return lead;
    }

    public String getAssigneeType() {
        return assigneeType;
    }

    public List<Component> getComponents() {
        return components;
    }

    public List<IssueType> getIssueTypes() {
        return issueTypes;
    }

    public List<Version> getVersions() {
        return versions;
    }

    public Map<String, Object> getRoles() {
        return roles;
    }
}
