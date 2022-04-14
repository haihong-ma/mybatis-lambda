package ma.haihong.mybatis.lambda.core.fuction;

import ma.haihong.mybatis.lambda.parser.func.SPredicate;

/**
 * @author haihong.ma
 */
public interface WhereFunction<T> {

    WhereFunction<T> where(SPredicate<T> predicate);
}
