package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.core.defaults.UpdateSet;
import ma.haihong.mybatis.lambda.parser.func.SPredicate;

import java.util.function.Consumer;

/**
 * @author haihong.ma
 */
public interface UpdateLambda<T> {

    int update(T entity, SPredicate<T> predicate);

    int update(Consumer<UpdateSet<T>> updateSet, SPredicate<T> predicate);
}
