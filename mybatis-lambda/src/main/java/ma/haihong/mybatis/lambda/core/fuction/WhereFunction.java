package ma.haihong.mybatis.lambda.core.fuction;

import ma.haihong.mybatis.lambda.core.combination.SelectFunctionAndAction;
import ma.haihong.mybatis.lambda.parser.func.SPredicate;

/**
 * @author haihong.ma
 */
public interface WhereFunction<T> {

    SelectFunctionAndAction<T> where(SPredicate<T> where);
}
