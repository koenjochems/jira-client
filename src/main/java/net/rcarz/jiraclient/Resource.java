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
 * A base class for JIRA resources.
 */
public abstract class Resource extends AResource {
    
    protected String self = null;

    protected Resource(String self, String id, String name, String key) {
    	super(id, name, key);
    	
    	this.self = self;
    }
    
    /**
     * Creates a new JIRA resource.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    public Resource(Map<String, Object> data) {
    	super(data);
 
    	if (data != null) {
    		self = Field.getString(data.get("self"));
    	}
    }

    /**
     * Resource URL.
     */
    public String getSelf() {
        return self;
    }
}
