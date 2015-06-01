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

import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

/**
 * Utility functions for translating between JSON and fields.
 */

public final class Field extends AField {

    /**
     * Field metadata structure.
     */
    public static final class Meta implements Comparable<Object> {
    	public String key;
        public boolean required;
        public String type;
        public String items;
        public String name;
        public String system;
        public String custom;
        public int customId;
        public List<AllowedValue> allowedValues;
        
        /**
    	 * A AllowedValue exception.
    	 */
    	public class AllowedValueException extends Exception {

    		private static final long serialVersionUID = 1L;

    		public AllowedValueException(String field, String value) {
    	        super("Value: " + value + " is not allowed for field: " + field);
    	    }
    	}
    	
        public Meta(String key, Map<String, Object> data) throws JiraException {
        	this.key = key;
        	
        	if (data != null) {
            	deserialise(data);
            }
        }

        /**
         * @param data Map of the JSON payload
         */
    	protected void deserialise(Map<String, Object> data) throws JiraException {
            Map<String, Object> schema = getMap(data.get("schema"));       
            if (schema == null) {
            	throw new JiraException("Field '" + key + "' is missing schema metadata");
            }
            
            this.required = Field.getBoolean(data.get("required"));
            this.name = Field.getString(data.get("name"));
            this.allowedValues = Field.getResourceArray(AllowedValue.class, data.get("allowedValues"));
            
            this.type = Field.getString(schema.get("type"));
            this.items = Field.getString(schema.get("items"));
            this.system = Field.getString(schema.get("system"));
            this.custom = Field.getString(schema.get("custom"));
            this.customId = Field.getInteger(schema.get("customId"));
        }
        
        @Override
        public String toString() {
            return name;
        }

		@Override
		public int compareTo(Object o) {
			return this.toString().compareTo(o.toString());
		}
		
		@Override
		public boolean equals(Object o)
	    {
	        if (o == null) return false;
	        if (o == this) return true;

	        return this.toString().equals(o.toString());
	    }
		
		/**
		 * Validate if the value is allowed for this field.
		 * 
		 * @param value
		 * @throws AllowedValueException 
		 */
		public void validateFieldValue(String value) throws AllowedValueException {
			for (AllowedValue av : allowedValues) {
				if (av.isAllowed(value)) {
					return;
				}
			}
			
			throw new AllowedValueException(name, value);
		}
    }

    /**
     * Field update operation.
     */
    public static final class Operation {
        public String name;
        public Object value;

        /**
         * Initialises a new update operation.
         *
         * @param name Operation name
         * @param value Field value
         */
        public Operation(String name, Object value) {
            this.name = name;
            this.value = value;
        }
    }

    /**
     * Allowed value types.
     */
    public enum ValueType {
        KEY("key"), NAME("name"), ID_NUMBER("id"), VALUE("value");
        private String typeName;

        private ValueType(String typeName) {
            this.typeName = typeName;
        }

        @Override
        public String toString() {
            return typeName;
        }
    };

    /**
     * Value and value type pair.
     */
    public static final class ValueTuple {
        public final String type;
        public final Object value;

        /**
         * Initialises the value tuple.
         *
         * @param type
         * @param value
         */
        public ValueTuple(String type, Object value) {
            this.type = type;
            this.value = value;
        }

        /**
         * Initialises the value tuple.
         *
         * @param type
         * @param value
         */
        public ValueTuple(ValueType type, Object value) {
            this(type.toString(), value);
        }
    }

    public static final String ASSIGNEE = "assignee";
    public static final String ATTACHMENT = "attachment";
    public static final String CHANGE_LOG = "changelog";
    public static final String CHANGE_LOG_ENTRIES = "histories";
    public static final String CHANGE_LOG_ITEMS = "items";
    public static final String COMMENT = "comment";
    public static final String COMPONENTS = "components";
    public static final String DESCRIPTION = "description";
    public static final String DUE_DATE = "duedate";
    public static final String FIX_VERSIONS = "fixVersions";
    public static final String ISSUE_LINKS = "issuelinks";
    public static final String ISSUE_TYPE = "issuetype";
    public static final String LABELS = "labels";
    public static final String PARENT = "parent";
    public static final String PRIORITY = "priority";
    public static final String PROJECT = "project";
    public static final String REPORTER = "reporter";
    public static final String RESOLUTION = "resolution";
    public static final String RESOLUTION_DATE = "resolutiondate";
    public static final String STATUS = "status";
    public static final String SUBTASKS = "subtasks";
    public static final String SUMMARY = "summary";
    public static final String TIME_TRACKING = "timetracking";
    public static final String VERSIONS = "versions";
    public static final String VOTES = "votes";
    public static final String WATCHES = "watches";
    public static final String WORKLOG = "worklog";
    public static final String TIME_ESTIMATE = "timeestimate";
    public static final String TIME_SPENT = "timespent";
    public static final String CREATED_DATE = "created";
    public static final String UPDATED_DATE = "updated";
    public static final String TRANSITION_TO_STATUS = "to";

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private Field() { }
    
    /**
     * Gets a date from the given object.
     *
     * @param d a string representation of a date
     *
     * @return a Date instance or null if d isn't a string
     */
    public static Date getDate(Object d) {
        Date result = null;

        if (d instanceof String) {
            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
            result = df.parse((String)d, new ParsePosition(0));
        }

        return result;
    }

    /**
     * Gets a date with a time from the given object.
     *
     * @param d a string representation of a date
     *
     * @return a Date instance or null if d isn't a string
     */
    public static Date getDateTime(Object d) {
        Date result = null;

        if (d instanceof String) {
            SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
            result = df.parse((String)d, new ParsePosition(0));
        }

        return result;
    }

    /**
     * Gets a JIRA resource from the given object.
     *
     * @param type Resource data type
     * @param r a JSONObject instance
     * @param restclient REST client instance
     *
     * @return a Resource instance or null if r isn't a JSONObject instance
     * @throws JiraException 
     */
	public static <T extends AResource> T getResource(Class<T> type, Object data) throws JiraException {

        T result = null;

        if (data instanceof Map) {
        	if (type == AllowedValue.class)
                result = (T)new AllowedValue((Map<String, Object>) data);
        	else if (type == Attachment.class)
                result = (T)new Attachment((Map<String, Object>) data);
            else if (type == ChangeLog.class)
                result = (T)new ChangeLog((Map<String, Object>) data);
            else if (type == ChangeLogEntry.class)
                result = (T)new ChangeLogEntry((Map<String, Object>) data);
            else if (type == ChangeLogItem.class)
                result = (T)new ChangeLogItem((Map<String, Object>) data);
            else if (type == Comment.class)
                result = (T)new Comment((Map<String, Object>) data);
            else if (type == Component.class)
                result = (T)new Component((Map<String, Object>) data);
            else if (type == CustomFieldOption.class)
                result = (T)new CustomFieldOption((Map<String, Object>) data);
            else if (type == Issue.class)
                result = (T)new Issue((Map<String, Object>) data);
            else if (type == IssueLink.class)
                result = (T)new IssueLink((Map<String, Object>) data);
            else if (type == IssueType.class)
                result = (T)new IssueType((Map<String, Object>) data);
            else if (type == LinkType.class)
                result = (T)new LinkType((Map<String, Object>) data);
            else if (type == Priority.class)
                result = (T)new Priority((Map<String, Object>) data);
            else if (type == Project.class)
                result = (T)new Project((Map<String, Object>) data);
            else if (type == RemoteLink.class)
                result = (T)new RemoteLink((Map<String, Object>) data);
            else if (type == Resolution.class)
                result = (T)new Resolution((Map<String, Object>) data);
            else if (type == Status.class)
                result = (T)new Status((Map<String, Object>) data);
            else if (type == Transition.class)
                result = (T)new Transition((Map<String, Object>) data);
            else if (type == User.class)
                result = (T)new User((Map<String, Object>) data);
            else if (type == Version.class)
                result = (T)new Version((Map<String, Object>) data);
            else if (type == Votes.class)
                result = (T)new Votes((Map<String, Object>) data);
            else if (type == Watches.class)
                result = (T)new Watches((Map<String, Object>) data);
            else if (type == WorkLog.class)
                result = (T)new WorkLog((Map<String, Object>) data);
        }

        return result;
    }

	/**
     * Gets a list of JIRA resources from the given object.
     *
     * @param type Resource data type
     * @param ra a JSONArray instance
     * @param restclient REST client instance
     *
     * @return a list of Resources found in ra
     * @throws JiraException 
     */
    public static <T extends AResource> List<T> getResourceArray(Class<T> type, Object data) throws JiraException {
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

    /**
     * Gets a time tracking object from the given object.
     *
     * @param tt a JSONObject instance
     *
     * @return a TimeTracking instance or null if tt isn't a JSONObject instance
     * @throws JiraException 
     */
    public static TimeTracking getTimeTracking(Object tt) throws JiraException {
        TimeTracking result = null;

        if (tt instanceof JSONObject && !((JSONObject)tt).isNullObject())
            result = new TimeTracking((JSONObject)tt);

        return result;
    }

    /**
     * Extracts field metadata from an editmeta Map object.
     *
     * @param name Field name
     * @param createmeta Edit metadata Map object
     *
     * @return a Meta instance with field metadata
     *
     * @throws JiraException when the field is missing or metadata is bad
     */
    public static Meta getFieldMetadata(String name, Map<String, Object> createmeta) throws JiraException {
        if (createmeta == null ) {
        	throw new JiraException("Field object is null");
        }
            
        Map<String, Object> f = getMap(createmeta.get(name));        
        if (f == null) {
        	throw new JiraException("Field '" + name + "' does not exist or read-only");
        }
                
        return new Meta(name, f);
    }

    /**
     * Converts the given value to a date.
     *
     * @param value New field value
     *
     * @return a Date instance or null
     */
    public static Date toDate(Object value) {
        if (value instanceof Date || value == null)
            return (Date)value;

        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
        return df.parse(value.toString(), new ParsePosition(0));
    }

    /**
     * Converts an iterable type to a JSON array.
     *
     * @param iter Iterable type containing field values
     * @param type Name of the item type
     *
     * @return a JSON-encoded array of items
     */
    public static JSONArray toArray(Iterable<?> iter, String type) throws JiraException {
        JSONArray results = new JSONArray();

        if (type == null)
            throw new JiraException("Array field metadata is missing item type");

        for (Object val : iter) {
            Operation oper = null;
            Object realValue = null;
            Object realResult = null;

            if (val instanceof Operation) {
                oper = (Operation)val;
                realValue = oper.value;
            } else
                realValue = val;

            if (type.equals("component") || type.equals("group") ||
                type.equals("user") || type.equals("version")) {

                JSONObject itemMap = new JSONObject();

                if (realValue instanceof ValueTuple) {
                    ValueTuple tuple = (ValueTuple)realValue;
                    itemMap.put(tuple.type, tuple.value.toString());
                } else
                    itemMap.put(ValueType.NAME.toString(), realValue.toString());

                realResult = itemMap;
            } else if (type.equals("string"))
                realResult = realValue.toString();

            if (oper != null) {
                JSONObject operMap = new JSONObject();
                operMap.put(oper.name, realResult);
                results.add(operMap);
            } else
                results.add(realResult);
        }

        return results;
    }

    /**
     * Converts the given value to a JSON object.
     *
     * @param name Field name
     * @param value New field value
     * @param createmeta Edit metadata JSON object
     *
     * @return a JSON-encoded field value
     *
     * @throws JiraException when a value is bad or field has invalid metadata
     * @throws UnsupportedOperationException when a field type isn't supported
     */
    public static Object toJson(String name, Object value, Meta createmeta)
        throws JiraException, UnsupportedOperationException {

        if (createmeta.type == null)
            throw new JiraException("Field metadata is missing a type");

        if (createmeta.type.equals("array")) {
            if (value == null)
                value = new ArrayList<Object>();
            else if (!(value instanceof Iterable))
                throw new JiraException("Field expects an Iterable value");

            return toArray((Iterable<?>)value, createmeta.items);
        } else if (createmeta.type.equals("date")) {
            if (value == null)
                return JSONNull.getInstance();

            Date d = toDate(value);
            if (d == null)
                throw new JiraException("Field expects a date value or format is invalid");

            SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
            return df.format(d);
        } else if (createmeta.type.equals("datetime")) {
            if (value == null)
                return JSONNull.getInstance();
            else if (!(value instanceof Timestamp))
                throw new JiraException("Field expects a Timestamp value");

            SimpleDateFormat df = new SimpleDateFormat(DATETIME_FORMAT);
            return df.format(value);
        } else if (createmeta.type.equals("issuetype") || createmeta.type.equals("priority") ||
        		createmeta.type.equals("user") || createmeta.type.equals("resolution")) {
            JSONObject json = new JSONObject();

            if (value == null)
                return JSONNull.getInstance();
            else if (value instanceof ValueTuple) {
                ValueTuple tuple = (ValueTuple)value;
                json.put(tuple.type, tuple.value.toString());
            } else
                json.put(ValueType.NAME.toString(), value.toString());

            return json.toString();
        } else if (createmeta.type.equals("project") || createmeta.type.equals("issuelink")) {
            JSONObject json = new JSONObject();

            if (value == null)
                return JSONNull.getInstance();
            else if (value instanceof ValueTuple) {
                ValueTuple tuple = (ValueTuple)value;
                json.put(tuple.type, tuple.value.toString());
            } else
                json.put(ValueType.KEY.toString(), value.toString());

            return json.toString();
        } else if (createmeta.type.equals("string") || (createmeta.type.equals("securitylevel"))) {
            if (value == null)
                return "";
            else if (value instanceof List)
                return ListToJSONObject((List<Object>) value);
            else if (value instanceof ValueTuple) {
                JSONObject json = new JSONObject();
                ValueTuple tuple = (ValueTuple)value;
                json.put(tuple.type, tuple.value.toString());
                return json.toString();
            }

            return value.toString();
        } else if (createmeta.type.equals("timetracking")) {
            if (value == null)
                return JSONNull.getInstance();
            else if (value instanceof TimeTracking)
                return ((TimeTracking) value).toJsonObject();
        } else if (createmeta.type.equals("number")) {
            if(!(value instanceof java.lang.Integer) && !(value instanceof java.lang.Double) && !(value 
                    instanceof java.lang.Float) && !(value instanceof java.lang.Long) )
            {
                throw new JiraException("Field expects a Numeric value");
            }
            return value;
        }

        throw new UnsupportedOperationException(createmeta.type + " is not a supported field type");
    }

    /**
     * Converts the given map to a JSON object.
     *
     * @param list List of values to be converted
     *
     * @return a JSON-encoded map
     */
    public static JSONObject ListToJSONObject(List<Object> data) {
        JSONObject json = new JSONObject();

        for (Object item : data) {
            if (item instanceof ValueTuple) {
                ValueTuple vt = (ValueTuple) item;
                json.put(vt.type, vt.value.toString());
            } else {
                json.put(ValueType.VALUE.toString(), item.toString());
            }
        }

        return json;
    }

    /**
     * Create a value tuple with value type of key.
     *
     * @param key The key value
     *
     * @return a value tuple
     */
    public static ValueTuple valueByKey(String key) {
        return new ValueTuple(ValueType.KEY, key);
    }

    /**
     * Create a value tuple with value type of name.
     *
     * @param name The name value
     *
     * @return a value tuple
     */
    public static ValueTuple valueByName(String name) {
        return new ValueTuple(ValueType.NAME, name);
    }

    /**
     * Create a value tuple with value type of ID number.
     *
     * @param id The ID number value
     *
     * @return a value tuple
     */
    public static ValueTuple valueById(String id) {
        return new ValueTuple(ValueType.ID_NUMBER, id);
    }
}
