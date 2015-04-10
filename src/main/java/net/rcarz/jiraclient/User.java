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
 * Represents a JIRA user.
 */
public class User extends Resource {

	public static final String URI = "user";
	
    private boolean active = false;
    private Map<String, Object> avatarUrls = null;
    private String displayName = null;
    private String email = null;

    /**
     * Creates a user from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected User(Map<String, Object> data) throws JiraException {
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
        active = Field.getBoolean(data.get("active"));
        avatarUrls = Field.getMap(data.get("avatarUrls"));
        displayName = Field.getString(data.get("displayName"));
        email = getEmailFromMap(data);
    }

    /**
     * API changes email address might be represented as either "email" or "emailAddress"
     *
     * @param map JSON object for the User
     * @return String email address of the JIRA user.
     */
    private String getEmailFromMap(Map<String, Object> data) {
        if (data.containsKey("email")) {
            return Field.getString(data.get("email"));
        } else {
            return Field.getString(data.get("emailAddress"));
        }
    }
    
    public boolean isActive() {
        return active;
    }

    public Map<String, Object> getAvatarUrls() {
        return avatarUrls;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }
}
