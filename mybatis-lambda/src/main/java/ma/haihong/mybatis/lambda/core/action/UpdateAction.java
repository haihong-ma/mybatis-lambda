package ma.haihong.mybatis.lambda.core.action;

import ma.haihong.mybatis.lambda.core.impl.UpdateSet;
import ma.haihong.mybatis.lambda.parsing.func.SPredicate;

import java.util.function.Consumer;

/**
 * @author haihong.ma
 */
public interface UpdateAction<T> {

    int update(T entity, SPredicate<T> where);

    int update(Consumer<UpdateSet<T>> updateSet, SPredicate<T> where);
}
