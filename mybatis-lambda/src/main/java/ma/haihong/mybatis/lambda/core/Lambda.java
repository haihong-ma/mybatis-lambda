package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.core.action.AllAction;
import ma.haihong.mybatis.lambda.core.fuction.SelectFunction;
import ma.haihong.mybatis.lambda.core.fuction.WhereFunction;

/**
 * @author haihong.ma
 */
public interface Lambda<T> extends AllAction<T>, SelectFunction<T>, WhereFunction<T> {
}
