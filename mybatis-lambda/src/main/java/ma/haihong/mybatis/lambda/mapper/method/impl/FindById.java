package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;

/**
 * @author haihong.ma
 */
public class FindById extends AbstractMethod {

    public FindById() {
        super(SqlTemplate.FIND_BY_ID);
    }

    @Override
    protected void doAddMappedStatement() {
        addSelectMappedStatement(tableInfo.getEntityClass());
    }

    @Override
    protected String initSqlScript() {
        return String.format(sqlTemplate.getSqlScript(), tableInfo.getAllColumnSqlSegment(), tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getKeyProperty());
    }
}
