package com.ddl.common.mybatis.dbshard;


import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

import com.ddl.common.mybatis.dbshard.util.SqlStringUtil;

@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class BatchUpdateForbiddenPlugin implements Interceptor {

    private static final String presentColumnTag = "presentColumn";// 定义where条件中必须出现的字段

    /**
     * 只对update语句进行拦截
     */
    public Object intercept(Invocation invocation) throws Throwable {
        // 只拦截update
        if (isUpdateMethod(invocation)) {
            invocation.getArgs()[0] = checkAndResetSQL(invocation);
        }
        return invocation.proceed();
    }

    /**
     * <p>
     * 判断该操作是否是update操作
     * </p>
     *
     * @return 是否是update操作
     */
    private boolean isUpdateMethod(Invocation invocation) {
        if (invocation.getArgs()[0] instanceof MappedStatement) {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            return SqlCommandType.UPDATE.equals(mappedStatement.getSqlCommandType());
        }
        return false;
    }

    /**
     * <p>
     * 检查update语句中是否定义了presentColumn，并且删除presentColumn后重新设置update语句
     * </p>
     *
     * @param invocation invocation实例
     * @return MappedStatement 返回删除presentColumn之后的MappedStatement实例
     */
    private Object checkAndResetSQL(Invocation invocation) {
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        mappedStatement.getSqlSource().getBoundSql(parameter);
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
        String resetSql = doCheckAndResetSQL(boundSql.getSql());
        return getMappedStatement(mappedStatement, boundSql, resetSql);
    }

    /**
     * <p>
     * 检查update语句中是否定义了presentColumn，并且删除presentColumn后重新设置update语句
     * </p>
     *
     * @param sql mapper中定义的sql语句(带有presentColumn的定义)
     * @return 删除presentColumn之后的sql
     */
    private String doCheckAndResetSQL(String sql) {
        if (sql.indexOf(presentColumnTag) > 0) {
            // presentColumn的定义是否在sql的最后面
            if (sql.indexOf("]") + 1 == sql.length()) {
                int startIndex = sql.indexOf("[");
                int endIndex = sql.indexOf("]");
                String presentColumnText = sql.substring(startIndex, endIndex + 1);// [presentColumn="orderNo"]
                // 剔除标记逻辑主键相关内容之后的sql，该sql才是真正执行update的sql语句
                sql = SqlStringUtil.replace(sql, presentColumnText, "");
                String[] subSqls = sql.toLowerCase().split("where");
                String[] keyWords = presentColumnText.split("\"");
                // 获取主键,比如orderNo
                String keyWord = keyWords[1];
                // 判断是否带有where条件并且在where条件中是否存在主键keyWord
                if (subSqls.length == 2 && subSqls[1].indexOf(keyWord) == -1) {
                    throw new IllegalArgumentException(
                            "该update语句:"
                                    + sql
                                    + "是批量更新sql，不允许执行。因为它的的where条件中未包含能表示主键的字段"
                                    + keyWord
                                    + ",所以会导致批量更新。");
                }
            } else {
                throw new IllegalArgumentException("[" + presentColumnTag + "=\"xxx\"\"]必须定义在update语句的最后面.");
            }
        } else {
            throw new IllegalArgumentException(
                    "在mapper文件中定义的update语句必须包含" + presentColumnTag
                            + "，它用于定义该sql的主键（逻辑主键或者业务主键），比如id");
        }
        return sql;
    }

    /**
     * <p>
     * 通过验证关键字段不能为空之后的sql重新构建mappedStatement
     * </p>
     *
     * @param mappedStatement 重新构造sql之前的mappedStatement实例
     * @param boundSql        重新构造sql之前的boundSql实例
     * @param resetSql        验证关键字段不能为空之后的sql
     * @return 重新构造之后的mappedStatement实例
     */
    private Object getMappedStatement(MappedStatement mappedStatement, BoundSql boundSql, String resetSql) {
        final BoundSql newBoundSql = new BoundSql(mappedStatement.getConfiguration(), resetSql,
                boundSql.getParameterMappings(), boundSql.getParameterObject());

        MappedStatement.Builder builder = new MappedStatement.Builder(mappedStatement.getConfiguration(),
                mappedStatement.getId(), new SqlSource() {
                        @Override
                    public BoundSql getBoundSql(Object parameterObject) {
                        return newBoundSql;
                    }
                }, mappedStatement.getSqlCommandType());

        builder.cache(mappedStatement.getCache());
        builder.fetchSize(mappedStatement.getFetchSize());
        builder.flushCacheRequired(mappedStatement.isFlushCacheRequired());
        builder.keyGenerator(mappedStatement.getKeyGenerator());
        //builder.keyProperty(mappedStatement.getKeyProperty());
        builder.resource(mappedStatement.getResource());
        builder.resultMaps(mappedStatement.getResultMaps());
        builder.resultSetType(mappedStatement.getResultSetType());
        builder.statementType(mappedStatement.getStatementType());
        builder.timeout(mappedStatement.getTimeout());
        builder.useCache(mappedStatement.isUseCache());
        return builder.build();
    }

    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    public void setProperties(Properties properties) {

    }

}