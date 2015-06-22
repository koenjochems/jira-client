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

import java.util.Map;

/**
 * Represents an issue type.
 */
public class IssueType extends Resource {

	public static final String URI = "issuetype";
    private String description = null;
    private String iconUrl = null;
    private boolean subtask = false;
    private Map<String, Object> fields = null;

    /**
     * Creates an issue type from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected IssueType(Map<String, Object> data) throws JiraException {
        super(data);

        if (data != null) {
        	deserialise(data);
        }
    }
    
    /**
     * @param data Map of the JSON payload
     */
    @Override
	protected void deserialise(Map<String, Object> data) {
    	description = Field.getString(data.get("description"));
        iconUrl = Field.getString(data.get("iconUrl"));
        subtask = Field.getBoolean(data.get("subtask"));
        fields = Field.getMap(data.get("fields"));
	}

    public String getDescription() {
        return description;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public boolean isSubtask() {
        return subtask;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

	@Override
	public String getValue() {
		return getName();
	}
}
