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
 * Represents an issue link.
 */
public class IssueLink extends Resource {

	public static final String URI = "issueLink";
    private LinkType type = null;
    private Issue inwardIssue = null;
    private Issue outwardIssue = null;

    /**
     * Creates a issue link from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected IssueLink(Map<String, Object> data) throws JiraException {
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
    	type = Field.getResource(LinkType.class, data.get("type"));
        outwardIssue = Field.getResource(Issue.class, data.get("outwardIssue"));
        inwardIssue = Field.getResource(Issue.class, data.get("inwardIssue"));
	}

    @Override
    public String toString() {
        return String.format("%s %s", getType().getInward(), getOutwardIssue());
    }

    public LinkType getType() {
        return type;
    }

    public Issue getOutwardIssue() {
        return outwardIssue;
    }
    
    public Issue getInwardIssue() {
        return inwardIssue;
    }
}
