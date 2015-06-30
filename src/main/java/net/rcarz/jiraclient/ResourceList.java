package net.rcarz.jiraclient;

import java.util.ArrayList;
import java.util.List;

public class ResourceList<T extends AResource> extends ArrayList<T> {
	private static final long serialVersionUID = 1890135681366032069L;
	
	public String getValue() {
		List<String> values = new ArrayList<String>();
		for (T item : this) {
			values.add(item.getValue());
		}
		
		return values.toString();
	}
}
