package net.rcarz.jiraclient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class IssueHistory extends Resource {

    private User user;
    private List<IssueHistoryItem> changes;
    private Date created;

    /**
     * Creates an issue history record from a JSON payload.
     *
     * @param data Map of the JSON payload
     * @throws JiraException 
     */
    protected IssueHistory(Map<String, Object> data) throws JiraException {
        super(data);

        if (data != null) {
        	deserialise(data);
        }
    }

    public IssueHistory(IssueHistory record, ArrayList<IssueHistoryItem> changes) {
        super(record.self, record.id, null, null);
        
        user = record.user;
        created = record.created;
        this.changes = changes;
    }
    
    /**
     * @param data Map of the JSON payload
     */
    @Override
	protected void deserialise(Map<String, Object> data) throws JiraException {
    	user = Field.getResource(User.class, data.get("author"));
        created = Field.getDateTime(data.get("created"));
        changes = Field.getResourceArray(IssueHistoryItem.class, data.get("items"));
	}

    public User getUser() {
        return user;
    }

    public List<IssueHistoryItem> getChanges() {
        return changes;
    }

    public Date getCreated() {
        return created;
    }
}
