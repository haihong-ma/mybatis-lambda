package ma.haihong.mybatis.lambda.parsing.func;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 支持序列化的Function
 *
 * @author haihong.ma
 */

@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
