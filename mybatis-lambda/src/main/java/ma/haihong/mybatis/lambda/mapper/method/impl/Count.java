package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;

/**
 * @author haihong.ma
 */
public class Count extends AbstractMethod {

    public Count() {
        super(SqlTemplate.COUNT);
    }

    @Override
    protected void doAddMappedStatement() {
        addSelectMappedStatement(Integer.class);
    }

    @Override
    protected String initSqlScript() {
        return String.format(sqlTemplate.getSqlScript(), "1", tableInfo.getTableName(), "");
    }
}
