package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;
import ma.haihong.mybatis.lambda.util.SqlScriptUtils;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.*;
import static ma.haihong.mybatis.lambda.constant.ParamConstants.COLLECTION;
import static ma.haihong.mybatis.lambda.constant.ParamConstants.ITEM;

/**
 * @author haihong.ma
 */
public class DeleteByIds extends AbstractMethod {

    public DeleteByIds() {
        super(SqlTemplate.DELETE_BY_IDS);
    }

    @Override
    protected void doAddMappedStatement() {
        addDeleteMappedStatement();
    }

    @Override
    protected String initSqlScript() {
        return String.format(sqlTemplate.getSqlScript(), tableInfo.getTableName(), tableInfo.getKeyColumn(),
                SqlScriptUtils.convertForeach(SqlScriptUtils.safeParam(ITEM), COLLECTION, null, ITEM, COMMA));
    }
}
