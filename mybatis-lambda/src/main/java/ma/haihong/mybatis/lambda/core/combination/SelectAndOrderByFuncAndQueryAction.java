package ma.haihong.mybatis.lambda.core.combination;

import ma.haihong.mybatis.lambda.core.action.AggregateAction;
import ma.haihong.mybatis.lambda.core.action.SelectAction;
import ma.haihong.mybatis.lambda.core.fuc.OrderByFunc;
import ma.haihong.mybatis.lambda.core.fuc.SelectFunc;

/**
 * @author haihong.ma
 */
public interface SelectAndOrderByFuncAndQueryAction<T> extends SelectFunc<T>, OrderByFunc<T>, SelectAction<T>, AggregateAction<T> {
}
