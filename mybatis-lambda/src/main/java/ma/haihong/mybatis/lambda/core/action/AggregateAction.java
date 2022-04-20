package ma.haihong.mybatis.lambda.core.action;

import ma.haihong.mybatis.lambda.parsing.func.SFunction;

import java.math.BigDecimal;

/**
 * @author haihong.ma
 */
public interface AggregateAction<T> {

    long count();

    long count(SFunction<T, ?> column);

    long count(SFunction<T, ?> column, boolean distinct);

    <R> R max(SFunction<T, R> column);

    <R> R min(SFunction<T, R> column);

    <R extends Number> BigDecimal sum(SFunction<T, R> column);
}
