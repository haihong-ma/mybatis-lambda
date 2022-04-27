package ma.haihong.mybatis.lambda.interfaces.fuc;

import ma.haihong.mybatis.lambda.interfaces.action.SelectAction;
import ma.haihong.mybatis.lambda.parsing.func.SFunction;

/**
 * @author haihong.ma
 */
public interface SelectFunc<T> {

    <R> SelectAction<R> select(SFunction<T, R> column);
}
