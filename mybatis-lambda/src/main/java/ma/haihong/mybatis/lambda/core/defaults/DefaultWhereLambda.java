package ma.haihong.mybatis.lambda.core.defaults;

import ma.haihong.mybatis.lambda.core.WhereLambda;
import ma.haihong.mybatis.lambda.mapper.LambdaMapper;
import ma.haihong.mybatis.lambda.parser.func.SPredicate;

import java.util.HashMap;

/**
 * @author haihong.ma
 */
public class DefaultWhereLambda<T> implements WhereLambda<T> {

    protected final LambdaMapper<T> mapper;

    private final StringBuilder sqlSegmentBuilder;
    private final HashMap<String, Object> lambdaParamMap;

    public DefaultWhereLambda(LambdaMapper<T> mapper) {
        this.mapper = mapper;
        this.lambdaParamMap = new HashMap<>();
        this.sqlSegmentBuilder = new StringBuilder();
    }

    @Override
    public WhereLambda<T> where(SPredicate<T> predicate) {
        lambdaParamMap.put("jobId", 1);
        sqlSegmentBuilder.append("job_id = #{ml.lambdaParamMap.jobId}");
        return this;
    }

    public String getSqlSegment() {
        return sqlSegmentBuilder.toString();
    }

    public HashMap<String, Object> getLambdaParamMap() {
        return lambdaParamMap;
    }
}
