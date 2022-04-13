package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;

/**
 * @author haihong.ma
 */
public class DeleteById extends AbstractMethod {

    public DeleteById() {
        super(SqlTemplate.DELETE_BY_ID);
    }

    @Override
    protected void doAddMappedStatement() {
        addDeleteMappedStatement();
    }

    @Override
    protected String initSqlScript() {
        return String.format(sqlTemplate.getSqlScript(), tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getKeyProperty());
    }
}
