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

import static ma.haihong.mybatis.lambda.constant.CommonConstants.*;

/**
 * @author haihong.ma
 */
public abstract class DefaultFunction<T> implements SelectFunction<T>, WhereFunction<T>, SelectAction<T>, SelectFunctionAndAction<T> {

    protected final LambdaMapper<T> mapper;

    protected SFunction<T, ?> selectFunc;
    private String selectSegment = EMPTY;

    private final StringBuilder whereSegmentBuilder;
    private final Map<String, Object> lambdaParamMap;

    public DefaultFunction(LambdaMapper<T> mapper) {
        this.mapper = mapper;
        this.lambdaParamMap = new HashMap<>();
        this.whereSegmentBuilder = new StringBuilder();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> SelectAction<R> select(SFunction<T, R> function) {
        Assert.notNull(function, "select function can not be null");
        this.selectFunc = function;
        SerializedLambda lambda = LambdaUtils.parse(function);
        this.selectSegment = TableUtils.propertyToColumn(ReflectionUtils.getClass(ReflectionUtils.convertNameWithDOT(lambda.getImplClass())), PropertyNamer.methodToProperty(lambda.getImplMethodName()));
        return (SelectAction<R>) this;
    }

    @Override
    public SelectFunctionAndAction<T> where(SPredicate<T> predicate) {
        Assert.notNull(predicate, "where predicate can not be null");
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
}
