package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.core.action.AllAction;
import ma.haihong.mybatis.lambda.core.fuc.OrderByFunc;
import ma.haihong.mybatis.lambda.core.fuc.SelectFunc;
import ma.haihong.mybatis.lambda.core.fuc.WhereFunc;

/**
 * @author haihong.ma
 */
public interface Lambda<T> extends AllAction<T>, SelectFunc<T>, WhereFunc<T>, OrderByFunc<T> {
}
