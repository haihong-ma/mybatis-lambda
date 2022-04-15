package ma.haihong.mybatis.lambda.core.fuc;

import ma.haihong.mybatis.lambda.core.combination.SelectAndOrderByFuncAndSelectAction;
import ma.haihong.mybatis.lambda.parser.func.SFunction;

/**
 * @author haihong.ma
 */
public interface OrderByFunc<T> {

    SelectAndOrderByFuncAndSelectAction<T> orderByAsc(SFunction<T, ?> column);

    SelectAndOrderByFuncAndSelectAction<T> orderByDesc(SFunction<T, ?> column);
}
