package ma.haihong.mybatis.lambda.core.fuc;

import ma.haihong.mybatis.lambda.core.action.SelectAction;
import ma.haihong.mybatis.lambda.parser.func.SFunction;

/**
 * @author haihong.ma
 */
public interface SelectFunc<T> {

    <R> SelectAction<R> select(SFunction<T, R> column);
}
