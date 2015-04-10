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

import net.rcarz.jiraclient.AField;
import net.rcarz.jiraclient.JiraException;
import net.rcarz.jiraclient.RestClient;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Utility functions for translating between JSON and fields.
 */
public final class GreenHopperField extends AField {

    public static final String DATE_TIME_FORMAT = "d/MMM/yy h:m a";
    public static final String NO_DATE = "None";

    private GreenHopperField() { }

    /**
     * Gets a date-time from the given object.
     *
     * @param dt Date-Time as a string
     *
     * @return the date-time or null
     */
    public static DateTime getDateTime(Object dt) {
        if(dt == null || ((String)dt).equals(NO_DATE)){
            return null;
        }
        return DateTime.parse((String)dt, DateTimeFormat.forPattern(DATE_TIME_FORMAT));
    }

    /**
     * Gets a GreenHopper resource from the given object.
     *
     * @param type Resource data type
     * @param r a JSONObject instance
     * @param restclient REST client instance
     *
     * @return a Resource instance or null if r isn't a JSONObject instance
     * @throws JiraException 
     */
    public static <T extends GreenHopperResource> T getResource(Class<T> type, Object data) throws JiraException {

        T result = null;
        
        if (data instanceof Map) {	
            if (type == Epic.class) {
                result = (T)new Epic((Map<String, Object>) data);
            } else if (type == EpicStats.class) {
                result = (T)new EpicStats((Map<String, Object>) data);
            } else if (type == EstimateStatistic.class) {
                result = (T)new EstimateStatistic((Map<String, Object>) data);
            } else if (type == EstimateSum.class) {
                result = (T)new EstimateSum((Map<String, Object>) data);
            } else if (type == GreenHopperIssue.class) {
                result = (T)new GreenHopperIssue((Map<String, Object>) data);
            } else if (type == Marker.class) {
                result = (T)new Marker((Map<String, Object>) data);
            } else if (type == RapidView.class) {
                result = (T)new RapidView((Map<String, Object>) data);
            } else if (type == RapidViewProject.class) {
                result = (T)new RapidViewProject((Map<String, Object>) data);
            } else if (type == Sprint.class) {
                result = (T)new Sprint((Map<String, Object>) data);
            } else if (type == SprintIssue.class) {
                result = (T)new SprintIssue((Map<String, Object>) data);
            }
        }

        return result;
    }

    /**
     * Gets a list of GreenHopper resources from the given object.
     *
     * @param type Resource data type
     * @param ra a JSONArray instance
     * @param restclient REST client instance
     *
     * @return a list of Resources found in ra
     * @throws JiraException 
     */
    public static <T extends GreenHopperResource> List<T> getResourceArray(Class<T> type, Object data) throws JiraException {
        List<T> results = new ArrayList<T>();

        if (data instanceof List) {
	        for (Object item : getList(data)) {
        	    T res = getResource(type, item);
	            if (res != null) {
	                results.add(res);
	            }
	        }
        }

        return results;
    }
}
