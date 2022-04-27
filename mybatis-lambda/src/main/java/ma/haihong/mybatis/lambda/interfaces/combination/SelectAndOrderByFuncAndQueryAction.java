package ma.haihong.mybatis.lambda.interfaces.combination;

import ma.haihong.mybatis.lambda.interfaces.action.AggregateAction;
import ma.haihong.mybatis.lambda.interfaces.action.SelectAction;
import ma.haihong.mybatis.lambda.interfaces.fuc.OrderByFunc;
import ma.haihong.mybatis.lambda.interfaces.fuc.SelectFunc;

/**
 * @author haihong.ma
 */
public interface SelectAndOrderByFuncAndQueryAction<T> extends SelectFunc<T>, OrderByFunc<T>, SelectAction<T>, AggregateAction<T> {
}
