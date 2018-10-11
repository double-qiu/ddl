/**
 * 
 */
package com.ddl.common.utils;

import java.util.UUID;

/**
 * 生成UUID工具类
 * 
 * @author 冯俊
 *
 */
public final class UuidUtils {

	/**
	 * 生成uuid
	 * 
	 * @return
	 */
	public static String getUuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}
