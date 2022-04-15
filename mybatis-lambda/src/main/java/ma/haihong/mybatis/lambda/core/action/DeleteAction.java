package ma.haihong.mybatis.lambda.core.action;

import ma.haihong.mybatis.lambda.parser.func.SPredicate;

/**
 * @author haihong.ma
 */
public interface DeleteAction<T> {

    int delete(SPredicate<T> where);
}
