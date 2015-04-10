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

import java.util.Date;
import java.util.Map;

/**
 * Represents an issue attachment.
 */
public class Attachment extends Resource {

	public static final String URI = "attachment";
	public static final String URI_MULTI = "attachments";
	
    private User author = null;
    private String filename = null;
    private Date created = null;
    private int size = 0;
    private String mimeType = null;
    private String content = null;

    /**
     * Creates an attachment from a JSON payload.
     *
     * @param data Map of the JSON payload
     * 
     * @throws JiraException 
     */
    protected Attachment(Map<String, Object> data) throws JiraException {
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
        filename = Field.getString(data.get("filename"));
        created = Field.getDate(data.get("created"));
        size = Field.getInteger(data.get("size"));
        mimeType = Field.getString(data.get("mimeType"));
        content = Field.getString(data.get("content"));
	}

    @Override
    public String toString() {
        return getContentUrl();
    }

    public User getAuthor() {
        return author;
    }

    public Date getCreatedDate() {
        return created;
    }

    public String getContentUrl() {
        return content;
    }

    public String getFileName() {
        return filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public int getSize() {
        return size;
    }
}
