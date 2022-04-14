package ma.haihong.mybatis.lambda.condition;

import ma.haihong.mybatis.lambda.mapper.LambdaMapper;

import java.util.HashMap;

/**
 * @author haihong.ma
 */
public class WhereLambda<T> {

    protected final LambdaMapper<T> mapper;
    private final StringBuilder sqlSegmentBuilder;
    private final HashMap<String, Object> lambdaParamMap;

    public WhereLambda(LambdaMapper<T> mapper) {
        this.mapper = mapper;
        this.lambdaParamMap = new HashMap<>();
        sqlSegmentBuilder = new StringBuilder();
        lambdaParamMap.put("jobId", 1);
        sqlSegmentBuilder.append("job_id = #{wl.lambdaParamMap.jobId}");
    }

    public String getSqlSegment() {
        return sqlSegmentBuilder.toString();
    }

    public HashMap<String, Object> getLambdaParamMap() {
        return lambdaParamMap;
    }
}
