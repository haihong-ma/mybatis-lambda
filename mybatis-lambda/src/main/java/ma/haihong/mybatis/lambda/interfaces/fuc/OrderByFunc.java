package ma.haihong.mybatis.lambda.interfaces.fuc;

import ma.haihong.mybatis.lambda.interfaces.combination.SelectAndOrderByFuncAndSelectAction;
import ma.haihong.mybatis.lambda.parsing.func.SFunction;

/**
 * @author haihong.ma
 */
public interface OrderByFunc<T> {

    SelectAndOrderByFuncAndSelectAction<T> orderByAsc(SFunction<T, ?> column);

    SelectAndOrderByFuncAndSelectAction<T> orderByDesc(SFunction<T, ?> column);
}
