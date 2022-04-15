package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.core.fuction.SelectFunction;
import ma.haihong.mybatis.lambda.core.fuction.WhereFunction;
import ma.haihong.mybatis.lambda.mapper.LambdaMapper;
import ma.haihong.mybatis.lambda.parser.func.SFunction;
import ma.haihong.mybatis.lambda.parser.func.SPredicate;

import java.util.HashMap;
import java.util.Map;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.EMPTY;

/**
 * @author haihong.ma
 */
public class DefaultFunction<T> implements SelectFunction<T>, WhereFunction<T> {

    protected final LambdaMapper<T> mapper;

    private final StringBuilder whereSegmentBuilder;
    private final Map<String, Object> lambdaParamMap;

    public DefaultFunction(LambdaMapper<T> mapper) {
        this.mapper = mapper;
        this.lambdaParamMap = new HashMap<>();
        this.whereSegmentBuilder = new StringBuilder();
    }

    @Override
    public <R> R select(SFunction<T, R> function) {
        return null;
    }

    @Override
    public WhereFunction<T> where(SPredicate<T> predicate) {
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
