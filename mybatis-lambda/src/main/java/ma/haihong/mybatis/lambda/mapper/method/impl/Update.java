package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;
import ma.haihong.mybatis.lambda.util.SqlScriptUtils;

import static ma.haihong.mybatis.lambda.constant.ParamConstants.*;

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
        String setSqlSegmentForEntity = SqlScriptUtils.convertIf(tableInfo.getSetSqlSegment(LAMBDA_DOT_ENTITY_DOT, true),
                SqlScriptUtils.objectNullableSqlSegment(LAMBDA_DOT_ENTITY), true);
        String setSqlSegmentForUpdateMap = SqlScriptUtils.convertIf(tableInfo.getSetSqlSegment(LAMBDA_DOT_UPDATE_MAP, false),
                SqlScriptUtils.objectNullableSqlSegment(LAMBDA_DOT_UPDATE_MAP), true);
        String setSqlSegment = setSqlSegmentForEntity + setSqlSegmentForUpdateMap;
        return String.format(sqlTemplate.getSqlScript(), tableInfo.getTableName(), setSqlSegment, SqlScriptUtils.lambdaSqlSegment(WHERE_SEGMENT));
    }
}
