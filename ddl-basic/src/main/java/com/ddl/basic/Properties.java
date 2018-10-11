package com.ddl.basic;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ClassName: Properties
 * 自定义配置文件属性获取
 *
 * @author DOUBLE
 */
@Configuration
@EnableAutoConfiguration
@ConfigurationProperties(prefix = "ddl")
public class Properties {

    private String mobileUrl;

    private String mobileKey;

	public String getMobileUrl() {
		return mobileUrl;
	}

	public void setMobileUrl(String mobileUrl) {
		this.mobileUrl = mobileUrl;
	}

	public String getMobileKey() {
		return mobileKey;
	}

	public void setMobileKey(String mobileKey) {
		this.mobileKey = mobileKey;
	}
}
