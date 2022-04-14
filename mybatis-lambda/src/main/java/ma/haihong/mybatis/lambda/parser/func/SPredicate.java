package ma.haihong.mybatis.lambda.parser.func;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * 支持序列化的Predicate
 *
 * @author haihong.ma
 */
@FunctionalInterface
public interface SPredicate<T> extends Predicate<T>, Serializable {
}
