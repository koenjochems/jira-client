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

import net.rcarz.jiraclient.AResource;
import net.rcarz.jiraclient.JiraException;

/**
 * A base class for GreenHopper resources.
 */
public abstract class GreenHopperResource extends AResource {

	public static final String RESOURCE_URI = "/rest/greenhopper/1.0/";
	
	/**
	 * @param data Map of the JSON payload
	 * @throws JiraException
	 */
    protected GreenHopperResource(Map<String, Object> data) throws JiraException {
		super(data);
	}   
}
