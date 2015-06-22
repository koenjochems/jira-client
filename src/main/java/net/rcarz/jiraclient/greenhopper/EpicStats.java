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

import java.util.Map;

import net.rcarz.jiraclient.JiraException;

/**
 * GreenHopper epic statistics.
 */
public class EpicStats extends GreenHopperResource {

    private Double notDoneEstimate = null;
    private Double doneEstimate = null;
    private int estimated = 0;
    private int notEstimated = 0;
    private int notDone = 0;
    private int done = 0;

    /**
     * Creates an estimate sum from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected EpicStats(Map<String, Object> data) throws JiraException {
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
    	notDoneEstimate = GreenHopperField.getDouble(data.get("notDoneEstimate"));
   		doneEstimate = GreenHopperField.getDouble(data.get("doneEstimate"));
       	estimated = GreenHopperField.getInteger(data.get("estimated"));
       	notEstimated = GreenHopperField.getInteger(data.get("notEstimated"));
       	notDone = GreenHopperField.getInteger(data.get("notDone"));
       	done = GreenHopperField.getInteger(data.get("done"));
	}

    public Double getNotDoneEstimate() {
        return notDoneEstimate;
    }

    public Double getDoneEstimate() {
        return doneEstimate;
    }

    public int getEstimated() {
        return estimated;
    }

    public int getNotEstimated() {
        return notEstimated;
    }

    public int getNotDone() {
        return notDone;
    }

    public int getDone() {
        return done;
    }

	@Override
	public String getValue() {
		return "Done: " + String.valueOf(getDone()) + " - Not done: " + String.valueOf(getNotDone());
	}
}
