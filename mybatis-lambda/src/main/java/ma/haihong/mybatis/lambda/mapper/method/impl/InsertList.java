package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.constant.ParamConstants;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;
import ma.haihong.mybatis.lambda.util.SqlScriptUtils;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.*;
import static ma.haihong.mybatis.lambda.constant.ParamConstants.ITEM;

/**
 * @author haihong.ma
 */
public class InsertList extends Insert {

    public InsertList() {
        super(SqlTemplate.INSERT_LIST);
    }

    @Override
    protected String initSqlScript() {
        String columnSegment = SqlScriptUtils.withBracket(tableInfo.getAllColumnSqlSegment());
        String valueSegment = SqlScriptUtils.withBracket(tableInfo.getInsertPropertySqlSegment(ITEM + DOT));
        String listValueSegment = SqlScriptUtils.convertForeach(valueSegment, ParamConstants.LIST, null, ITEM, COMMA);
        return String.format(sqlTemplate.getSqlScript(), tableInfo.getTableName(), columnSegment, listValueSegment);
    }
}
