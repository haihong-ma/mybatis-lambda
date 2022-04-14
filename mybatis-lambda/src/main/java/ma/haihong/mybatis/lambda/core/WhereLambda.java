package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.parser.func.SPredicate;

/**
 * @author haihong.ma
 */
public interface WhereLambda<T> {

    WhereLambda<T> where(SPredicate<T> predicate);
}
