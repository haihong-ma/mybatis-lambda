package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.exception.MybatisLambdaException;
import ma.haihong.mybatis.lambda.interfaces.action.MapAction;
import ma.haihong.mybatis.lambda.interfaces.action.SelectAction;
import ma.haihong.mybatis.lambda.interfaces.combination.AfterWhereFuncOrAction;
import ma.haihong.mybatis.lambda.interfaces.combination.AfterOrderByFuncOrAction;
import ma.haihong.mybatis.lambda.interfaces.fuc.OrderByFunc;
import ma.haihong.mybatis.lambda.interfaces.fuc.SelectFunc;
import ma.haihong.mybatis.lambda.interfaces.fuc.WhereFunc;
import ma.haihong.mybatis.lambda.mapper.Mapper;
import ma.haihong.mybatis.lambda.parsing.LambdaUtils;
import ma.haihong.mybatis.lambda.parsing.func.SFunction;
import ma.haihong.mybatis.lambda.parsing.func.SPredicate;
import ma.haihong.mybatis.lambda.parsing.model.ParsedResult;
import ma.haihong.mybatis.lambda.parsing.model.PropertyInfo;
import ma.haihong.mybatis.lambda.util.Assert;
import ma.haihong.mybatis.lambda.util.TableUtils;

import java.util.HashMap;
import java.util.Map;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.*;
import static ma.haihong.mybatis.lambda.constant.SqlConstants.*;

/**
 * @author haihong.ma
 */
public abstract class DefaultFunc<T> implements SelectFunc<T>, WhereFunc<T>, OrderByFunc<T>,
        SelectAction<T>, MapAction<T>, AfterWhereFuncOrAction<T>, AfterOrderByFuncOrAction<T> {

    protected final LambdaMapper<T> mapper;

    protected SFunction<T, ?> selectFunc;
    private String selectSegment = EMPTY;

    private final StringBuilder orderBySegment;

    private String whereSegment;
    private Map<String, Object> paramMap;

    protected static final String WHERE_PREDICATE_NULL_TIP = "where predicate can't be null";
    protected static final String SELECT_FUNCTION_NULL_TIP = "select column can't be null";

    @SuppressWarnings("unchecked")
    public DefaultFunc(Mapper<T> mapper) {
        if (!(mapper instanceof LambdaMapper)) {
            throw new MybatisLambdaException("invalid mapper define,please extends BaseMapper");
        }
        this.whereSegment = EMPTY;
        this.paramMap = new HashMap<>();
        this.mapper = (LambdaMapper<T>) mapper;
        this.orderBySegment = new StringBuilder();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> SelectAction<R> select(SFunction<T, R> column) {
        assertSelectFunctionNotNull(column);
        this.selectFunc = column;
        this.selectSegment = convertToColumnName(column);
        return (SelectAction<R>) this;
    }

    @Override
    public AfterWhereFuncOrAction<T> where(SPredicate<T> where) {
        assertWherePredicateNotNull(where);
        ParsedResult result = LambdaUtils.parse(where);
        paramMap = result.getParamMap();
        whereSegment = result.getSqlSegment();
        return this;
    }

    @Override
    public AfterOrderByFuncOrAction<T> orderByAsc(SFunction<T, ?> column) {
        assertSelectFunctionNotNull(column);
        orderBySegment.append(convertToColumnName(column)).append(SPACE).append(ASC).append(COMMA);
        return this;
    }

    @Override
    public AfterOrderByFuncOrAction<T> orderByDesc(SFunction<T, ?> column) {
        assertSelectFunctionNotNull(column);
        orderBySegment.append(convertToColumnName(column)).append(SPACE).append(DESC).append(COMMA);
        return this;
    }

    public String getSelectSegment() {
        return selectSegment;
    }

    public String getWhereSegment() {
        return whereSegment;
    }

    public String getGroupBySegment() {
        return EMPTY;
    }

    public String getOrderBySegment() {
        return orderBySegment.length() > 0 ? ORDER_BY + SPACE + orderBySegment.deleteCharAt(orderBySegment.lastIndexOf(COMMA)) : EMPTY;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    protected void setSelectSegment(String selectSegment) {
        this.selectSegment = selectSegment;
    }

    protected String convertToColumnName(SFunction<T, ?> column) {
        PropertyInfo propertyInfo = LambdaUtils.parse(column);
        return TableUtils.propertyToColumn(propertyInfo.getEntityClass(), propertyInfo.getPropertyName());
    }

    protected void assertWherePredicateNotNull(SPredicate<T> where) {
        Assert.notNull(where, WHERE_PREDICATE_NULL_TIP);
    }

    protected void assertSelectFunctionNotNull(SFunction<T, ?> column) {
        Assert.notNull(column, SELECT_FUNCTION_NULL_TIP);
    }

}
