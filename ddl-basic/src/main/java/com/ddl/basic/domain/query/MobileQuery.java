package com.ddl.basic.domain.query;

public class MobileQuery {

	private String url;
	
	private String phone;
	
	private String key;
	
	
	public MobileQuery() {
		super();
	}

	public MobileQuery(String url, String phone, String key) {
		super();
		this.url = url;
		this.phone = phone;
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDtype() {
		return "json";
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
