package ma.haihong.mybatis.lambda.core.impl;

import ma.haihong.mybatis.lambda.core.action.SelectAction;
import ma.haihong.mybatis.lambda.core.combination.SelectFunctionAndAction;
import ma.haihong.mybatis.lambda.core.fuction.SelectFunction;
import ma.haihong.mybatis.lambda.core.fuction.WhereFunction;
import ma.haihong.mybatis.lambda.mapper.LambdaMapper;
import ma.haihong.mybatis.lambda.parser.LambdaUtils;
import ma.haihong.mybatis.lambda.parser.func.SFunction;
import ma.haihong.mybatis.lambda.parser.func.SPredicate;
import ma.haihong.mybatis.lambda.util.Assert;
import ma.haihong.mybatis.lambda.util.ReflectionUtils;
import ma.haihong.mybatis.lambda.util.TableUtils;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.lang.invoke.SerializedLambda;
import java.util.HashMap;
import java.util.Map;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.EMPTY;

/**
 * @author haihong.ma
 */
public abstract class DefaultFunction<T> implements SelectFunction<T>, WhereFunction<T>, SelectAction<T>, SelectFunctionAndAction<T> {

    protected final LambdaMapper<T> mapper;

    protected SFunction<T, ?> selectFunc;
    private String selectSegment = EMPTY;

    private final StringBuilder whereSegmentBuilder;
    private final Map<String, Object> lambdaParamMap;

    protected static final String WHERE_PREDICATE_NULL_TIP = "where predicate can't be null";
    protected static final String SELECT_FUNCTION_NULL_TIP = "select column can't be null";

    public DefaultFunction(LambdaMapper<T> mapper) {
        this.mapper = mapper;
        this.lambdaParamMap = new HashMap<>();
        this.whereSegmentBuilder = new StringBuilder();
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
    public SelectFunctionAndAction<T> where(SPredicate<T> where) {
        assertWherePredicateNotNull(where);
        lambdaParamMap.put("jobId", 1);
        whereSegmentBuilder.append("job_id = #{ml.lambdaParamMap.jobId}");
        return this;
    }

    public String getTablePrefix() {
        return EMPTY;
    }

    public String getSelectSegment() {
        return selectSegment;
    }

    public String getJoinSegment() {
        return EMPTY;
    }

    public String getWhereSegment() {
        return whereSegmentBuilder.toString();
    }

    public String getGroupBySegment() {
        return EMPTY;
    }

    public String getOrderBySegment() {
        return EMPTY;
    }

    public Map<String, Object> getLambdaParamMap() {
        return lambdaParamMap;
    }

    protected void setSelectSegment(String selectSegment) {
        this.selectSegment = selectSegment;
    }

    protected String convertToColumnName(SFunction<T, ?> column) {
        SerializedLambda lambda = LambdaUtils.parse(column);
        Class<?> entityClass = ReflectionUtils.getClass(ReflectionUtils.convertNameWithDOT(lambda.getImplClass()));
        Assert.notNull(entityClass, "entity class resolve failed");
        return TableUtils.propertyToColumn(entityClass, PropertyNamer.methodToProperty(lambda.getImplMethodName()));
    }

    protected void assertWherePredicateNotNull(SPredicate<T> where) {
        Assert.notNull(where, WHERE_PREDICATE_NULL_TIP);
    }

    protected void assertSelectFunctionNotNull(SFunction<T, ?> column) {
        Assert.notNull(column, SELECT_FUNCTION_NULL_TIP);
    }

}
