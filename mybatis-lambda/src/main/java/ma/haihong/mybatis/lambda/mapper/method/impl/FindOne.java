package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;
import ma.haihong.mybatis.lambda.util.SqlScriptUtils;

import static ma.haihong.mybatis.lambda.constant.ParamConstants.*;

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
        addSelectMappedStatement(getResultType());
    }

    @Override
    protected String initSqlScript() {
        String whereSegment = SqlScriptUtils.convertIf(SqlScriptUtils.convertWhere(SqlScriptUtils.unSafeParam(LAMBDA_DOT + WHERE_SEGMENT)), SqlScriptUtils.objectNullableSqlSegment(LAMBDA), true);
        String groupBySegment = SqlScriptUtils.lambdaSqlSegment(GROUP_BY_SEGMENT);
        String orderBySegment = SqlScriptUtils.lambdaSqlSegment(ORDER_BY_SEGMENT);
        String selectChooseSegment = SqlScriptUtils.convertChoose(SqlScriptUtils.stringNullableSqlSegment(LAMBDA_DOT + SELECT_SEGMENT),
                SqlScriptUtils.unSafeParam(LAMBDA_DOT + SELECT_SEGMENT), tableInfo.getAllColumnSqlSegment());
        String selectSegment = SqlScriptUtils.convertIf(selectChooseSegment, SqlScriptUtils.objectNullableSqlSegment(LAMBDA), true);
        return String.format(sqlTemplate.getSqlScript(), selectSegment, tableInfo.getTableName(), whereSegment, groupBySegment, orderBySegment);
    }

    protected Class<?> getResultType() {
        return tableInfo.getEntityClass();
    }
}
