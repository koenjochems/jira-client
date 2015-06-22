package net.rcarz.jiraclient;

import java.util.Map;

public class IssueHistoryItem extends Resource {

    private String field;
    private String from;
    private String to;
    private String fromStr;
    private String toStr;

    /**
     * 
     * @param data Map of the JSON payload
     * 
     * @throws JiraException
     */
    public IssueHistoryItem(Map<String, Object> data) throws JiraException {
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
    	field = Field.getString(data.get("field"));
        from = Field.getString(data.get("from"));
        to = Field.getString(data.get("to"));
        fromStr = Field.getString(data.get("fromString"));
        toStr = Field.getString(data.get("toString"));
	}

    public String getField() {
        return field;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getFromStr() {
        return fromStr;
    }

    public String getToStr() {
        return toStr;
    }

	@Override
	public String getValue() {
		return getFromStr() + " -> " + getToStr();
	}
}
