package ma.haihong.mybatis.lambda.core.defaults;

import ma.haihong.mybatis.lambda.core.Lambda;
import ma.haihong.mybatis.lambda.mapper.LambdaMapper;
import ma.haihong.mybatis.lambda.parser.func.SPredicate;

/**
 * @author haihong.ma
 */
public class DefaultLambda<T> extends DefaultUpdateLambda<T> implements Lambda<T> {

    public DefaultLambda(LambdaMapper<T> mapper) {
        super(mapper);
    }

    @Override
    public int delete(SPredicate<T> predicate) {
        super.where(predicate);
        return mapper.delete(this);
    }
}
