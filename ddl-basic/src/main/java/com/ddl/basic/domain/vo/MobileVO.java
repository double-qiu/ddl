package com.ddl.basic.domain.vo;

import java.io.Serializable;

public class MobileVO implements Serializable{

	private static final long serialVersionUID = -8112897917256328335L;

	private String province;//省份
	
	private String city;//城市
	
	private String areacode;//区号
	
	private String zip;
	
	private String company;//运营商
	
	private String card;

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAreacode() {
		return areacode;
	}

	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	@Override
	public String toString() {
		return "MobileVO [province=" + province + ", city=" + city + ", areacode=" + areacode + ", zip=" + zip
				+ ", company=" + company + ", card=" + card + "]";
	}
}
