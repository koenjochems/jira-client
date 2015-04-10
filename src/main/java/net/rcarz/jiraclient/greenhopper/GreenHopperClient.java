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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rcarz.jiraclient.AClient;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.RestClient;

/**
 * A GreenHopper extension to the JIRA client.
 */
public class GreenHopperClient extends AClient {

    /**
     * Creates a GreenHopper client.
     *
     * @param jira JIRA client
     */
    public GreenHopperClient(RestClient restclient) {
    	super(restclient);
    }

    /**
     * Retreives the rapid view with the given ID.
     *
     * @param id Rapid View ID
     *
     * @return a RapidView instance
     *
     * @throws JiraException when something goes wrong
     */
    public RapidView getRapidView(int id) throws JiraException {
        try {
        	return GreenHopperField.getResource(RapidView.class, restclient.get(RapidView.URI, String.valueOf(id)));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve rapid view " + id, ex);
        }
    }

    /**
     * Retreives all rapid views visible to the session user.
     *
     * @return a list of rapid views
     *
     * @throws JiraException when something goes wrong
     */
    public List<RapidView> getRapidViews() throws JiraException {
    	try {
    		return GreenHopperField.getResourceArray(RapidView.class, 
    			GreenHopperField.getMap(restclient.get(RapidView.URI).get("views")));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve rapid views", ex);
        }
    }
    
    /**
     * Retrieves the backlog data for the given rapid view.
     *
     * @param rv Rapid View instance
     *
     * @return the backlog
     *
     * @throws JiraException when the retrieval fails
     */
    public Backlog getBacklog(RapidView rv) throws JiraException {
        try {
        	Map<String, String> queryParams = new HashMap<String, String>();
        	queryParams.put("rapidViewId", rv.getId());
        	return GreenHopperField.getResource(Backlog.class, restclient.get(queryParams, Backlog.URI));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve backlog data", ex);
        }
    }
    
    /**
     * Retrieves all sprints associated with this rapid view.
     *
     * @return a list of sprints
     *
     * @throws JiraException when the retrieval fails
     */
    public List<Sprint> getSprints(RapidView rv) throws JiraException {
        try {
        	return GreenHopperField.getResourceArray(Sprint.class, 
        		GreenHopperField.getList(restclient.get(Sprint.URI, rv.getId()).get("sprints")));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve sprints", ex);
        }
    }
    
    /**
     * Retrieves the sprint report for the given rapid view and sprint.
     *
     * @param rv Rapid View instance
     * @param sprint Sprint instance
     *
     * @return the sprint report
     *
     * @throws JiraException when the retrieval fails
     */
    public SprintReport get(RapidView rv, Sprint sprint) throws JiraException {
        try {
        	Map<String, String> queryParams = new HashMap<String, String>();
        	queryParams.put("rapidViewId", rv.getId());
        	queryParams.put("sprintId", sprint.getId());
        	return GreenHopperField.getResource(SprintReport.class, 
        		GreenHopperField.getMap(restclient.get(queryParams, SprintReport.URI).get("contents")));
        } catch (Exception ex) {
            throw new JiraException("Failed to retrieve sprint report", ex);
        }
    }
}
