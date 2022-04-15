package ma.haihong.mybatis.lambda.core.impl;

import ma.haihong.mybatis.lambda.core.SelectFunctionAndAction;
import ma.haihong.mybatis.lambda.core.action.AllAction;
import ma.haihong.mybatis.lambda.core.fuction.SelectFunction;
import ma.haihong.mybatis.lambda.core.fuction.WhereFunction;
import ma.haihong.mybatis.lambda.mapper.LambdaMapper;
import ma.haihong.mybatis.lambda.parser.func.SFunction;
import ma.haihong.mybatis.lambda.parser.func.SPredicate;
import ma.haihong.mybatis.lambda.util.Assert;

import java.util.HashMap;
import java.util.Map;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.EMPTY;

/**
 * @author haihong.ma
 */
public abstract class DefaultFunction<T> implements SelectFunction<T>, WhereFunction<T>, AllAction<T>, SelectFunctionAndAction<T> {

    protected SFunction<T, ?> selectFunc;
    protected final LambdaMapper<T> mapper;

    private final StringBuilder whereSegmentBuilder;
    private final Map<String, Object> lambdaParamMap;

    public DefaultFunction(LambdaMapper<T> mapper) {
        this.mapper = mapper;
        this.lambdaParamMap = new HashMap<>();
        this.whereSegmentBuilder = new StringBuilder();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> AllAction<R> select(SFunction<T, R> function) {
        Assert.notNull(function, "select function can not be null");
        selectFunc = function;
        return (AllAction<R>) this;
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
        return EMPTY;
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
