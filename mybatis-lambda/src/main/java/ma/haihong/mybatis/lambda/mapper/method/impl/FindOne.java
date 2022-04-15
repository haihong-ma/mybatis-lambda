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
        addSelectMappedStatement(tableInfo.getEntityClass());
    }

    @Override
    protected String initSqlScript() {
        String tablePrefix = SqlScriptUtils.lambdaSqlSegment(TABLE_PREFIX);
        String joinSegment = SqlScriptUtils.lambdaSqlSegment(JOIN_SEGMENT);
        String whereSegment = SqlScriptUtils.lambdaSqlSegment(WHERE_SEGMENT);
        String groupBySegment = SqlScriptUtils.lambdaSqlSegment(GROUP_BY_SEGMENT);
        String orderBySegment = SqlScriptUtils.lambdaSqlSegment(ORDER_BY_SEGMENT);
        String selectChooseSegment = SqlScriptUtils.convertChoose(SqlScriptUtils.stringNullableSqlSegment(LAMBDA_DOT + SELECT_SEGMENT),
                SqlScriptUtils.unSafeParam(LAMBDA_DOT + SELECT_SEGMENT), tableInfo.getAllColumnSqlSegment());
        String selectSegment = SqlScriptUtils.convertIf(selectChooseSegment, SqlScriptUtils.objectNullableSqlSegment(LAMBDA), true);
        return String.format(sqlTemplate.getSqlScript(), selectSegment,
                tablePrefix + tableInfo.getTableName(), joinSegment, whereSegment, groupBySegment, orderBySegment);
    }
}
