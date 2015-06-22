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
 * Represents an issue component.
 */
public class Component extends Resource {
    public static final String URI = "component";
    
    private String description = null;
    private boolean isAssigneeTypeValid = false;

    /**
     * Creates a component from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected Component(Map<String, Object> data) throws JiraException {
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
        isAssigneeTypeValid = Field.getBoolean(data.get("isAssigneeTypeValid"));
    }

    public String getDescription() {
        return description;
    }

    public boolean isAssigneeTypeValid() {
        return isAssigneeTypeValid;
    }

	@Override
	public String getValue() {
		return getName();
	}
}

