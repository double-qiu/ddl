package com.ddl.common.mybatis.dbshard;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.ArrayList;
import java.util.List;

public class DruidSqlParser {
    private SQLStatement sqlStatement;
    private String sqlOriginal;

    public DruidSqlParser(String sql) {
        MySqlStatementParser mySqlStatementParser = new MySqlStatementParser(sql);
        this.sqlStatement = mySqlStatementParser.parseStatement();

        this.sqlOriginal = sql;
    }

    public List<String> getTableName() {
        List<String> tableNames = new ArrayList<>();

        if (sqlStatement instanceof SQLSelectStatement) {
            SQLSelectStatement stmt = (SQLSelectStatement) sqlStatement;
            SQLSelectQueryBlock query = (SQLSelectQueryBlock) stmt.getSelect().getQuery();
            if (query.getFrom() instanceof SQLExprTableSource) {
                SQLExprTableSource re = (SQLExprTableSource) query.getFrom();
                System.out.println(re.getExpr().toString());

                tableNames.add(re.getExpr().toString());
                return tableNames;
            } else {
                SQLJoinTableSource tableSource = (SQLJoinTableSource) query.getFrom();
                getTableNameNested(tableSource, tableNames);
            }
        } else if (sqlStatement instanceof MySqlInsertStatement) {
            tableNames.add(((MySqlInsertStatement) sqlStatement).getTableName().getSimpleName());
        } else if (sqlStatement instanceof MySqlUpdateStatement) {
            tableNames.add(((MySqlUpdateStatement) sqlStatement).getTableName().getSimpleName());
        } else if (sqlStatement instanceof MySqlDeleteStatement) {
            tableNames.add(((MySqlDeleteStatement) sqlStatement).getTableName().getSimpleName());
        }
        return tableNames;
    }

    private void getTableNameNested(SQLJoinTableSource tableSource, List<String> tn) {
        SQLTableSource leftS = tableSource.getLeft();
        SQLTableSource rightS = tableSource.getRight();
        if (leftS instanceof SQLExprTableSource) {
            SQLExprTableSource sts = (SQLExprTableSource) leftS;
            tn.add(sts.getExpr().toString());
        } else {
            getTableNameNested((SQLJoinTableSource) leftS, tn);
        }
        if (rightS instanceof SQLExprTableSource) {
            SQLExprTableSource sts = (SQLExprTableSource) rightS;
            tn.add(sts.getExpr().toString());
        } else {
            getTableNameNested((SQLJoinTableSource) rightS, tn);
        }
    }

    public String toSql() {
        return SQLUtils.toMySqlString(sqlStatement);
    }

    public String getSqlOriginal() {
        return this.sqlOriginal;
    }

}
