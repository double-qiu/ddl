package com.ddl.common.mybatis.dbshard;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ddl.common.mybatis.dbshard.util.ReflectionUtil;


/**
 * 分库分表策略
 */
public class ShardStrategy {

    private String key = "schoolId";
    private Integer tableCount = 16;
    private Integer dbCount = 8;

    /**
     */
    public ShardStrategy(String key, String tableCount, String dbCount) {
        this.key = key;
        this.tableCount = Integer.parseInt(tableCount);
        this.dbCount = Integer.parseInt(dbCount);
    }

    /**
     * 获取分表策略后的表名
     *
     * @param tableName tableName
     * @param params    params
     * @author Jason
     */
    public String getShardTableName(String tableName, Object params) {

        StringBuilder builder = new StringBuilder(tableName);

        Long keyVal = obtainParam(params);

        builder.append("_");
        builder.append(keyVal % tableCount);

        return builder.toString();
    }

    /**
     * 获取分库策略后的库名
     */
    public String getShardDBName(String dbName, Object params) {

        StringBuilder builder = new StringBuilder(dbName);

        Long keyVal = obtainParam(params);

        builder.append("_");
        builder.append(keyVal % dbCount + 1);

        return builder.toString();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Long obtainParam(Object params) {
        Object paramVal = null;
        if (params instanceof String || params instanceof Integer || params instanceof Byte || params instanceof Date) {
            paramVal = params;
        } else if (params instanceof Map) {
            if (((Map) params).containsKey("list") || ((Map) params).containsKey("collection")) {
                List list = (ArrayList) ((Map<String, Object>) params).get("collection");
                paramVal = ReflectionUtil.getFieldValue(list.get(0), key);
            } else {
                paramVal = ((Map<String, Object>) params).get(key);
            }

        } else {
            paramVal = ReflectionUtil.getFieldValue(params, key);
        }
        if (paramVal != null) {
            return Long.parseLong(paramVal.toString());
        }
        return 0L;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getTableCount() {
        return tableCount;
    }

    public void setTableCount(Integer tableCount) {
        this.tableCount = tableCount;
    }

    public Integer getDbCount() {
        return dbCount;
    }

    public void setDbCount(Integer dbCount) {
        this.dbCount = dbCount;
    }
}
