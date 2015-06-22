/**
 * jira-client - a simple JIR7A REST client
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

import java.util.Date;
import java.util.Map;

/**
 * Represents an issue work log.
 */
public class WorkLog extends Resource {

	public static final String URI = "worklog";
	
    private User author = null;
    private String comment = null;
    private Date created = null;
    private Date updated = null;
    private User updatedAuthor = null;
    private int timeSpentSeconds = 0;

    /**
     * Creates a work log from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected WorkLog(Map<String, Object> data) throws JiraException {
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
        author = Field.getResource(User.class, data.get("author"));
        comment = Field.getString(data.get("comment"));
        created = Field.getDate(data.get("created"));
        updated = Field.getDate(data.get("updated"));
        updatedAuthor = Field.getResource(User.class, data.get("updatedAuthor"));
        timeSpentSeconds = Field.getInteger(data.get("timeSpentSeconds"));
    }

    @Override
    public String toString() {
        return created + " by " + author;
    }

    public User getAuthor() {
        return author;
    }

    public String getComment() {
        return comment;
    }

    public Date getCreatedDate() {
        return created;
    }

    public User getUpdateAuthor() {
        return updatedAuthor;
    }

    public Date getUpdatedDate() {
        return updated;
    }

    public int getTimeSpentSeconds() {
        return timeSpentSeconds;
    }

	@Override
	public String getValue() {
		return getAuthor() + ": " + getComment();
	}
}
