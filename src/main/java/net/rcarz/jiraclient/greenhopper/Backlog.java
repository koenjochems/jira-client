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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.rcarz.jiraclient.JiraException;

/**
 * GreenHopper backlog data.
 */
public class Backlog extends GreenHopperResource {

	public static final String URI = RESOURCE_URI + "xboard/plan/backlog/data";
	
    private List<SprintIssue> issues = null;
    private List<SprintIssue> backlogIssues = null;
    private int rankCustomFieldId = 0;
    private List<Sprint> sprints = null;
    private List<RapidViewProject> projects = null;
    private List<Marker> markers = null;
    private List<Epic> epics = null;
    private boolean canEditEpics = false;
    private boolean canManageSprints = false;
    private boolean maxIssuesExceeded = false;
    private int queryResultLimit = 0;
    private Map<String, List<RapidViewVersion>> versionsPerProject = null;

    /**
     * Creates the backlog from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected Backlog(Map<String, Object> data) throws JiraException {
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
    	issues = GreenHopperField.getResourceArray(SprintIssue.class, data.get("issues"));
        rankCustomFieldId = GreenHopperField.getInteger(data.get("rankCustomFieldId"));
        sprints = GreenHopperField.getResourceArray(Sprint.class, data.get("sprints"));
        projects = GreenHopperField.getResourceArray(RapidViewProject.class, data.get("projects"));
        markers = GreenHopperField.getResourceArray(Marker.class, data.get("markers"));     
        canManageSprints = GreenHopperField.getBoolean(data.get("canManageSprints"));
        maxIssuesExceeded = GreenHopperField.getBoolean(data.get("maxIssuesExceeded"));
        queryResultLimit = GreenHopperField.getInteger(data.get("queryResultLimit"));
        
    	Map<String, Object> epicData = GreenHopperField.getMap(data.get("epicData"));
    	if (epicData != null) {
	       	epics = GreenHopperField.getResourceArray(Epic.class, epicData.get("epics"));
	   		canEditEpics = GreenHopperField.getBoolean(epicData.get("canEditEpics"));
    	}

        Map<String, Object> verData = GreenHopperField.getMap(data.get("versionData"));
        if (verData != null) {
	        Map<String, Object> verMap = GreenHopperField.getMap(verData.get("versionsPerProject"));
	        if (verMap != null) {
	        	versionsPerProject = new HashMap<String, List<RapidViewVersion>>();
	        
		        for (String key : verMap.keySet()) {
		            if (!(verMap.get(key) instanceof List))
		                continue;

		            versionsPerProject.put(key, GreenHopperField.getResourceArray(RapidViewVersion.class, verMap.get(key)));
		        }
	        }
        }

        // determining which issues are actually in the backlog vs the sprints
        // fill in the issues into the single sprints and the backlog issue list respectively
        for (SprintIssue issue : issues) {
            boolean addedToSprint = false;
            for (Sprint sprint : sprints) {
                if (sprint.getIssuesIds().contains(issue.getId())) {
                    sprint.getIssues().add(issue);
                    addedToSprint = true;
                }
            }
            
            if (!addedToSprint) {
                if (backlogIssues == null) {
                    backlogIssues = new ArrayList<SprintIssue>();
                }
                backlogIssues.add(issue);
            }
        }
	}

    public List<SprintIssue> getIssues() {
        return issues;
    }

    public List<SprintIssue> getBacklogIssues() {
        return backlogIssues;
    }

    public int getRankCustomFieldId() {
        return rankCustomFieldId;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public List<RapidViewProject> getProjects() {
        return projects;
    }

    public List<Marker> getMarkers() {
        return markers;
    }

    public List<Epic> getEpics() {
        return epics;
    }

    public boolean canEditEpics() {
        return canEditEpics;
    }

    public boolean canManageSprints() {
        return canManageSprints;
    }

    public boolean maxIssuesExceeded() {
        return maxIssuesExceeded;
    }

    public int queryResultLimit() {
        return queryResultLimit;
    }

    public Map<String, List<RapidViewVersion>> getVersionsPerProject() {
        return versionsPerProject;
    }
}
