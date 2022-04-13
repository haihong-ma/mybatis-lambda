package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;
import ma.haihong.mybatis.lambda.util.SqlScriptUtils;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.COMMA;
import static ma.haihong.mybatis.lambda.constant.ParamConstants.COLLECTION;
import static ma.haihong.mybatis.lambda.constant.ParamConstants.ITEM;

/**
 * @author haihong.ma
 */
public class FindByIds extends AbstractMethod {

    public FindByIds() {
        super(SqlTemplate.FIND_BY_IDS);
    }

    @Override
    protected void doAddMappedStatement() {
        addSelectMappedStatement(tableInfo.getEntityClass());
    }

    @Override
    protected String initSqlScript() {
        return String.format(sqlTemplate.getSqlScript(), tableInfo.getAllColumnSqlSegment(), tableInfo.getTableName(),
                tableInfo.getKeyColumn(), SqlScriptUtils.convertForeach(SqlScriptUtils.safeParam(ITEM), COLLECTION, null, ITEM, COMMA));
    }
}
