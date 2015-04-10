package net.rcarz.jiraclient;

import java.util.Map;

/**
 * Abstract jira resource class that provides the common resource items.
 * 
 * @author Koen Jochems
 *
 */
public abstract class AResource {
	protected String id = null;
	protected String name = null;
	protected String key = null;
	
	/**
	 * Resource constructor
	 * 
	 * @param jira resource id
	 * @param resource name
	 * @param resource key
	 */
	protected AResource(String id, String name, String key) {
		this.id = id;
		this.name = name;
		this.key = key;
	}
	
	/**
	 * Resource constructor
	 * 
	 * @param data Map of the JSON payload
	 */
	protected AResource(Map<String, Object> data) {		
		if (data != null) {
			id = Field.getString(data.get("id"));
	   		name = Field.getString(data.get("name"));
	   		key = Field.getString(data.get("key"));
		}
    }
    
	/**
	 * Implement this method and call from the constructor to initialize the parameters
	 *  
	 * @param data Map of the JSON payload
	 * 
	 * @throws JiraException
	 */
	protected abstract void deserialise(Map<String, Object> data) throws JiraException;
	
    /**
     * Internal JIRA ID.
     */
    public String getId() {
        return id;
    }
    
    /**
     * 
     * @return resource name
     */
    public String getName() {
        return name;
    }
    
    /**
     * 
     * @return resource key
     */
    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return getName();
    }
}
