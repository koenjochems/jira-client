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

import java.util.List;
import java.util.Map;

import net.rcarz.jiraclient.JiraException;

/**
 * GreenHopper sprint statistics.
 */
public class SprintReport extends GreenHopperResource {

	public static final String URI = RESOURCE_URI + "rapid/charts/sprintreport";
	
    private Sprint sprint = null;
    private List<SprintIssue> completedIssues = null;
    private List<SprintIssue> incompletedIssues = null;
    private List<SprintIssue> puntedIssues = null;
    private EstimateSum completedIssuesEstimateSum = null;
    private EstimateSum incompletedIssuesEstimateSum = null;
    private EstimateSum allIssuesEstimateSum = null;
    private EstimateSum puntedIssuesEstimateSum = null;
    private List<String> issueKeysAddedDuringSprint = null;

    /**
     * Creates a sprint report from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected SprintReport(Map<String, Object> data) throws JiraException {
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
    	sprint = GreenHopperField.getResource(Sprint.class, data.get("sprint"));
        completedIssues = GreenHopperField.getResourceArray(SprintIssue.class, data.get("completedIssues"));
        incompletedIssues = GreenHopperField.getResourceArray(SprintIssue.class, data.get("incompletedIssues"));
        puntedIssues = GreenHopperField.getResourceArray(SprintIssue.class, data.get("puntedIssues"));
        completedIssuesEstimateSum = GreenHopperField.getResource(EstimateSum.class, data.get("completedIssuesEstimateSum"));
        incompletedIssuesEstimateSum = GreenHopperField.getResource(EstimateSum.class, data.get("incompletedIssuesEstimateSum"));
        allIssuesEstimateSum = GreenHopperField.getResource(EstimateSum.class, data.get("allIssuesEstimateSum"));
        puntedIssuesEstimateSum = GreenHopperField.getResource(EstimateSum.class, data.get("puntedIssuesEstimateSum"));      
        issueKeysAddedDuringSprint = GreenHopperField.getStringArray(data.get("issueKeysAddedDuringSprint"));
	}

    public Sprint getSprint() {
        return sprint;
    }

    public List<SprintIssue> getCompletedIssues() {
        return completedIssues;
    }

    public List<SprintIssue> getIncompletedIssues() {
        return incompletedIssues;
    }

    public List<SprintIssue> getPuntedIssues() {
        return puntedIssues;
    }

    public EstimateSum getCompletedIssuesEstimateSum() {
        return completedIssuesEstimateSum;
    }

    public EstimateSum getIncompletedIssuesEstimateSum() {
        return incompletedIssuesEstimateSum;
    }

    public EstimateSum getAllIssuesEstimateSum() {
        return allIssuesEstimateSum;
    }

    public EstimateSum getPuntedIssuesEstimateSum() {
        return puntedIssuesEstimateSum;
    }

    public List<String> getIssueKeysAddedDuringSprint() {
        return issueKeysAddedDuringSprint;
    }

	@Override
	public String getValue() {
		return getSprint().getValue();
	}
}
