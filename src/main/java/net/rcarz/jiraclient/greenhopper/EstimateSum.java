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
 * GreenHopper estimate sum for rapid views.
 */
public class EstimateSum extends GreenHopperResource {

    private Double value = null;
    private String text = null;

    /**
     * Creates an estimate sum from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected EstimateSum(Map<String, Object> data) throws JiraException {
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
    	value = GreenHopperField.getDouble(data.get("value"));
   		text = GreenHopperField.getString(data.get("text"));
	}

    public Double getSumValue() {
        return value;
    }

    public String getText() {
        return text;
    }

	@Override
	public String getValue() {
		return String.valueOf(getSumValue());
	}
}
