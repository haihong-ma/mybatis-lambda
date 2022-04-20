package ma.haihong.mybatis.lambda.core.fuc;

import ma.haihong.mybatis.lambda.core.combination.SelectAndOrderByFuncAndQueryAction;
import ma.haihong.mybatis.lambda.parsing.func.SPredicate;

/**
 * @author haihong.ma
 */
public interface WhereFunc<T> {

    SelectAndOrderByFuncAndQueryAction<T> where(SPredicate<T> where);
}
