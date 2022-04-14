package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;
import ma.haihong.mybatis.lambda.util.SqlScriptUtils;

/**
 * @author haihong.ma
 */
public class Delete extends AbstractMethod {

    public Delete() {
        super(SqlTemplate.DELETE);
    }

    @Override
    protected void doAddMappedStatement() {
        addDeleteMappedStatement();
    }

    @Override
    protected String initSqlScript() {
        return String.format(sqlTemplate.getSqlScript(), tableInfo.getTableName(), SqlScriptUtils.lambdaSqlSegment());
    }
}
