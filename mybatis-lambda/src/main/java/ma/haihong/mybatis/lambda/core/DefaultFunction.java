package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.core.fuction.SelectFunction;
import ma.haihong.mybatis.lambda.core.fuction.WhereFunction;
import ma.haihong.mybatis.lambda.mapper.LambdaMapper;
import ma.haihong.mybatis.lambda.parser.func.SFunction;
import ma.haihong.mybatis.lambda.parser.func.SPredicate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author haihong.ma
 */
public class DefaultFunction<T> implements SelectFunction<T>, WhereFunction<T> {

    protected final LambdaMapper<T> mapper;

    private final StringBuilder sqlSegmentBuilder;
    private final Map<String, Object> lambdaParamMap;

    public DefaultFunction(LambdaMapper<T> mapper) {
        this.mapper = mapper;
        this.lambdaParamMap = new HashMap<>();
        this.sqlSegmentBuilder = new StringBuilder();
    }

    @Override
    public Object select(SFunction<T, ?> function) {
        return null;
    }

    @Override
    public WhereFunction<T> where(SPredicate<T> predicate) {
        lambdaParamMap.put("jobId", 1);
        sqlSegmentBuilder.append("job_id = #{ml.lambdaParamMap.jobId}");
        return this;
    }

    public String getSqlSegment() {
        return sqlSegmentBuilder.toString();
    }

    public Map<String, Object> getLambdaParamMap() {
        return lambdaParamMap;
    }
}
