package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.parsing.LambdaUtils;
import ma.haihong.mybatis.lambda.parsing.func.SFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author haihong.ma
 */
public class UpdateSet<T> {
    private final Map<SFunction<T, ?>, Object> updateMap = new HashMap<>();

    public UpdateSet<T> set(SFunction<T, ?> column, Object value) {
        updateMap.put(column, value);
        return this;
    }

    public UpdateSet<T> set(boolean condition, SFunction<T, ?> column, Object value) {
        if (condition) {
            return set(column, value);
        }
        return this;
    }

    Map<String, Object> getUpdateMap() {
        Map<String, Object> paramMap = new HashMap<>();
        updateMap.forEach((key, value) -> paramMap.put(LambdaUtils.parse(key).getPropertyName(), value));
        return paramMap;
    }
}
