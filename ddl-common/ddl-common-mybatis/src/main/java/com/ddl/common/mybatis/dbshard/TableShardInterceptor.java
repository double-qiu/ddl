package com.ddl.common.mybatis.dbshard;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ddl.common.mybatis.dbshard.util.ReflectionUtil;

@Intercepts(@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}))
public class TableShardInterceptor implements Interceptor {
    private final Logger logger = LoggerFactory.getLogger(TableShardInterceptor.class);
    /**
     * 需要分表策略的map key为目标表 value为分表规则ShardStrategy,可以执行继承。
     */
    private Map<String, ShardStrategy> tableNamesForShard = new HashMap<>();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sqlOriginal = boundSql.getSql();

        for (Map.Entry<String, ShardStrategy> entry : tableNamesForShard.entrySet()) {
            String tableName = entry.getKey();
            Matcher matcher = Pattern.compile(tableName).matcher(sqlOriginal);
            if (matcher.find()) {
                if (boundSql.getParameterObject() == null) {
                    logger.error(tableName + "属于分表体系，需要必须的分表参数");
                    throw new SQLException(tableName + "属于分表体系，需要必须的分表参数");
                }
                ShardStrategy strategy = entry.getValue();

                String newTableName = strategy.getShardTableName(tableName, boundSql.getParameterObject());

                sqlOriginal = sqlOriginal.replaceAll(tableName, newTableName);
            }

        }

        ReflectionUtil.setFieldValue(boundSql, "sql", sqlOriginal);

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    /**
     * Don‘t Modify Any Code
     */
    @Override
    public void setProperties(Properties properties) {
        Enumeration<?> enu = properties.propertyNames();
        while (enu.hasMoreElements()) {
            String key = (String) enu.nextElement();
            String value = properties.getProperty(key);
            if (value != null && value.length() > 0) {
                String[] ps = value.split(":");
                if (ps.length == 3) {
                    ShardStrategy strategy = new ShardStrategy(ps[0], ps[1], ps[2]);

                    tableNamesForShard.put(key, strategy);
                }
            }
        }

    }
}