package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;

/**
 * @author haihong.ma
 */
public class UpdateById extends AbstractMethod {

    public UpdateById() {
        super(SqlTemplate.UPDATE_BY_ID);
    }

    @Override
    protected void doAddMappedStatement() {
        addUpdateMappedStatement();
    }

    @Override
    protected String initSqlScript() {
        String setSqlSegment = tableInfo.getSetSqlSegment();
        return String.format(sqlTemplate.getSqlScript(), tableInfo.getTableName(),
                setSqlSegment, tableInfo.getKeyColumn(), tableInfo.getKeyProperty());
    }

    @Override
    protected Class<?> getParameterType() {
        return tableInfo.getEntityClass();
    }
}
