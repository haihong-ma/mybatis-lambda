package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;
import ma.haihong.mybatis.lambda.util.SqlScriptUtils;

import static ma.haihong.mybatis.lambda.constant.ParamConstants.LAMBDA_ENTITY_DOT;

/**
 * @author haihong.ma
 */
public class Update extends AbstractMethod {

    public Update() {
        super(SqlTemplate.UPDATE);
    }

    @Override
    protected void doAddMappedStatement() {
        addUpdateMappedStatement();
    }

    @Override
    protected String initSqlScript() {
        String setSqlSegment = tableInfo.getSetSqlSegment(LAMBDA_ENTITY_DOT);
        return String.format(sqlTemplate.getSqlScript(), tableInfo.getTableName(), setSqlSegment, SqlScriptUtils.lambdaSqlSegment());
    }
}
