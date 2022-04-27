package ma.haihong.mybatis.lambda.interfaces.action;

import ma.haihong.mybatis.lambda.parsing.func.SFunction;

import java.util.Map;

/**
 * @author haihong.ma
 */
public interface MapAction<T> {
    <K, V> Map<K, V> toMap(SFunction<T, K> keyColumn, SFunction<T, V> valueColumn);
}
