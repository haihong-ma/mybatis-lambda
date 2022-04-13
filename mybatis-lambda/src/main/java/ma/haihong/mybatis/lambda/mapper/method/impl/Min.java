package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;

/**
 * @author haihong.ma
 */
public class Min extends AbstractMethod {

    public Min() {
        super(SqlTemplate.MIN);
    }

    @Override
    protected void doAddMappedStatement() {

    }

    @Override
    protected String initSqlScript() {
        return null;
    }
}
