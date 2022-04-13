package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;

/**
 * @author haihong.ma
 */
public class Max extends AbstractMethod {

    public Max() {
        super(SqlTemplate.MAX);
    }

    @Override
    protected void doAddMappedStatement() {

    }

    @Override
    protected String initSqlScript() {
        return null;
    }
}
