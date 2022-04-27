package ma.haihong.mybatis.lambda.interfaces;

import ma.haihong.mybatis.lambda.interfaces.action.AllAction;
import ma.haihong.mybatis.lambda.interfaces.fuc.OrderByFunc;
import ma.haihong.mybatis.lambda.interfaces.fuc.SelectFunc;
import ma.haihong.mybatis.lambda.interfaces.fuc.WhereFunc;

/**
 * @author haihong.ma
 */
public interface Lambda<T> extends AllAction<T>, SelectFunc<T>, WhereFunc<T>, OrderByFunc<T> {
}
