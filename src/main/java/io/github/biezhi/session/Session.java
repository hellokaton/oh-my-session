package io.github.biezhi.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Session implements Serializable {

	private Map<String, Object> attribute = new HashMap<String, Object>(8);
	private String id;
	private String host;
	private long created;
	private long lasted;
	private boolean first;

	public Map<String, Object> getAttribute() {
		return attribute;
	}

	public void setAttribute(Map<String, Object> attribute) {
		this.attribute = attribute;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getLasted() {
		return lasted;
	}

	public void setLasted(long lasted) {
		this.lasted = lasted;
	}

	public boolean isFirst() {
		return first;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public void put(String key, Object value){
		attribute.put(key, value);
	}

	public Object get(String key){
		return attribute.get(key);
	}

	public Set<String> keySet(){
		return attribute.keySet();
	}

	public void remove(String key){
		attribute.remove(key);
	}

	public String getHost() {
		return host;
	}

	public Session setHost(String host) {
		this.host = host;
		return this;
	}
}
