package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;

/**
 * @author haihong.ma
 */
public class FindOne extends AbstractMethod {

    public FindOne() {
        super(SqlTemplate.FIND_ONE);
    }

    public FindOne(SqlTemplate sqlTemplate) {
        super(sqlTemplate);
    }

    @Override
    protected void doAddMappedStatement() {
    }

    @Override
    protected String initSqlScript() {
        return null;
    }
}
