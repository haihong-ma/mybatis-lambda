package ma.haihong.mybatis.lambda.interfaces.fuc;

import ma.haihong.mybatis.lambda.interfaces.combination.AfterOrderByFuncOrAction;
import ma.haihong.mybatis.lambda.parsing.func.SFunction;

/**
 * @author haihong.ma
 */
public interface OrderByFunc<T> {

    AfterOrderByFuncOrAction<T> orderByAsc(SFunction<T, ?> column);

    AfterOrderByFuncOrAction<T> orderByDesc(SFunction<T, ?> column);
}
