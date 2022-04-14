package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.parser.func.SPredicate;

/**
 * @author haihong.ma
 */
public interface DeleteLambda<T> {
    int delete(SPredicate<T> predicate);
}
