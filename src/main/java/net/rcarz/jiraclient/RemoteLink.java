package net.rcarz.jiraclient;

import java.util.Map;

public class RemoteLink extends Resource {
	
	public static final String URI = "remotelink";
	
    private String remoteUrl;
    private String title;

    /**
     * 
     * @param data Map of the JSON payload
     * @throws JiraException
     */
    public RemoteLink(Map<String, Object> data) throws JiraException {
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
    	Map<String, Object> object = Field.getMap(data.get("object"));
        
    	if (object != null) {
	        remoteUrl = Field.getString(object.get("url"));
	        title = Field.getString(object.get("title"));
    	}
    }

    public String getTitle() {
        return title;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }
}
