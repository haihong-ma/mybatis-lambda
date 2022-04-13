package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;

/**
 * @author haihong.ma
 */
public class Delete extends AbstractMethod {

    public Delete() {
        super(SqlTemplate.DELETE);
    }

    @Override
    protected void doAddMappedStatement() {

    }

    @Override
    protected String initSqlScript() {
        return null;
    }
}
