package ma.haihong.mybatis.lambda.condition;

import ma.haihong.mybatis.lambda.mapper.LambdaMapper;

/**
 * @author haihong.ma
 */
public class Lambda<T> extends UpdateLambda<T> {
    public Lambda(LambdaMapper<T> mapper) {
        super(mapper);
    }
}
