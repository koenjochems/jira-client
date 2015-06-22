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

package net.rcarz.jiraclient.greenhopper;

import java.util.List;
import java.util.Map;

import net.rcarz.jiraclient.JiraException;

/**
 * A base class for GreenHopper issues.
 */
public class GreenHopperIssue extends GreenHopperResource {

    private boolean hidden = false;
    private String summary = null;
    private String typeName = null;
    private String typeId = null;
    private String typeUrl = null;
    private String priorityUrl = null;
    private String priorityName = null;
    private boolean done = false;
    private String assignee = null;
    private String assigneeName = null;
    private String avatarUrl = null;
    private String colour = null;
    private String statusId = null;
    private String statusName = null;
    private String statusUrl = null;
    private List<Integer> fixVersions = null;
    private int projectId = 0;

    /**
     * Creates an issue from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected GreenHopperIssue(Map<String, Object> data) throws JiraException {
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
    	hidden = GreenHopperField.getBoolean(data.get("hidden"));
        summary = GreenHopperField.getString(data.get("summary"));
        typeName = GreenHopperField.getString(data.get("key"));
        typeId = GreenHopperField.getString(data.get("typeId"));
        typeUrl = GreenHopperField.getString(data.get("typeUrl"));
        priorityUrl = GreenHopperField.getString(data.get("priorityUrl"));
        priorityName = GreenHopperField.getString(data.get("priorityName"));
        done = GreenHopperField.getBoolean(data.get("done"));
        assignee = GreenHopperField.getString(data.get("assignee"));
        assigneeName = GreenHopperField.getString(data.get("assigneeName"));
        avatarUrl = GreenHopperField.getString(data.get("avatarUrl"));
        colour = GreenHopperField.getString(data.get("color"));
        statusId = GreenHopperField.getString(data.get("statusId"));
        statusName = GreenHopperField.getString(data.get("statusName"));
        statusUrl = GreenHopperField.getString(data.get("statusUrl"));
        fixVersions = GreenHopperField.getIntegerArray(data.get("fixVersions"));
        projectId = GreenHopperField.getInteger(data.get("projectId"));
	}

    @Override
    public String toString() {
        return key;
    }
    
    public Boolean isHidden() {
        return hidden;
    }

    public String getSummary() {
        return summary;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getTypeUrl() {
        return typeUrl;
    }

    public String getPriorityUrl() {
        return priorityUrl;
    }

    public String getPriorityName() {
        return priorityName;
    }

    public Boolean isDone() {
        return done;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getColour() {
        return colour;
    }

    public String getStatusId() {
        return statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getStatusUrl() {
        return statusUrl;
    }

    public List<Integer> getFixVersions() {
        return fixVersions;
    }

    public int getProjectId() {
        return projectId;
    }

	@Override
	public String getValue() {
		return getKey();
	}
}
