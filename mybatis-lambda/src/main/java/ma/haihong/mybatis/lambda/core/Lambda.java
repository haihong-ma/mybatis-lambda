package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.core.action.DeleteAction;
import ma.haihong.mybatis.lambda.core.action.SelectAction;
import ma.haihong.mybatis.lambda.core.action.UpdateAction;
import ma.haihong.mybatis.lambda.core.fuction.SelectFunction;
import ma.haihong.mybatis.lambda.core.fuction.WhereFunction;

/**
 * @author haihong.ma
 */
public interface Lambda<T> extends SelectAction<T>, UpdateAction<T>, DeleteAction<T>,
        SelectFunction<T>, WhereFunction<T> {
}
