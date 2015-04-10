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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.rcarz.jiraclient.JiraException;

import org.joda.time.DateTime;

/**
 * Represents a GreenHopper sprint.
 */
public class Sprint extends GreenHopperResource {

	public static final String URI = RESOURCE_URI + "sprintquery";
	
	private boolean closed = false;
    private DateTime startDate = null;
    private DateTime endDate = null;
    private DateTime completeDate = null;
    private List<Integer> issuesIds = null;
    private List<SprintIssue> issues = null;

    /**
     * Creates a sprint from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected Sprint(Map<String, Object> data) throws JiraException {
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
    	closed = data.containsValue("CLOSED");
        startDate = GreenHopperField.getDateTime(data.get("startDate"));
        endDate = GreenHopperField.getDateTime(data.get("endDate"));
        completeDate = GreenHopperField.getDateTime(data.get("completeDate"));
        issuesIds = GreenHopperField.getIntegerArray(data.get("issuesIds"));
	}

    public Boolean isClosed() {
        return closed;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public DateTime getCompleteDate() {
        return completeDate;
    }

    public List<SprintIssue> getIssues(){
        if (issues == null) {
            issues = new ArrayList<SprintIssue>();
        }
        return issues;
    }

    public List<Integer> getIssuesIds() {
        return issuesIds;
    }
}
