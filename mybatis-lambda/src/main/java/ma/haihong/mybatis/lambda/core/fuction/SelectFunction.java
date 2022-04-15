package ma.haihong.mybatis.lambda.core.fuction;

import ma.haihong.mybatis.lambda.core.action.SelectAction;
import ma.haihong.mybatis.lambda.parser.func.SFunction;

/**
 * @author haihong.ma
 */
public interface SelectFunction<T> {

    <R> SelectAction<R> select(SFunction<T, R> function);
}
