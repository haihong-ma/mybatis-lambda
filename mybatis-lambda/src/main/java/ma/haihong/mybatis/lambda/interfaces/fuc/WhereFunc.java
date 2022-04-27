package ma.haihong.mybatis.lambda.interfaces.fuc;

import ma.haihong.mybatis.lambda.interfaces.combination.AfterWhereFuncOrAction;
import ma.haihong.mybatis.lambda.parsing.func.SPredicate;

/**
 * @author haihong.ma
 */
public interface WhereFunc<T> {

    AfterWhereFuncOrAction<T> where(SPredicate<T> where);
}
