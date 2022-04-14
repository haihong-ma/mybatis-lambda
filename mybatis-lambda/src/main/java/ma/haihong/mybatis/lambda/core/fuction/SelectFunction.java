package ma.haihong.mybatis.lambda.core.fuction;

import ma.haihong.mybatis.lambda.parser.func.SFunction;

/**
 * @author haihong.ma
 */
public interface SelectFunction<T> {
    Object select(SFunction<T, ?> function);
}
