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
 * Represents an issue link type.
 */
public class LinkType extends Resource {

	public static final String URI = "issueLinkType";
	
	private String inward = null;
    private String outward = null;

    /**
     * Creates a issue link type from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected LinkType(Map<String, Object> data) throws JiraException {
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
        inward = Field.getString(data.get("inward"));
        outward = Field.getString(data.get("outward"));
    }

    public String getInward() {
        return inward;
    }

    public String getOutward() {
        return outward;
    }
}
