package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.core.action.AllAction;
import ma.haihong.mybatis.lambda.core.fuction.SelectFunction;

/**
 * @author haihong.ma
 */
public interface SelectFunctionAndAction<T> extends SelectFunction<T>, AllAction<T> {
}
