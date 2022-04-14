package ma.haihong.mybatis.lambda.parser;

import ma.haihong.mybatis.lambda.exception.MybatisLambdaException;
import ma.haihong.mybatis.lambda.parser.func.SFunction;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author haihong.ma
 */
public class LambdaUtils {

    private static final String SERIALIZABLE_WRITE_REPLACE_METHOD = "writeReplace";
    private static final Map<String, WeakReference<SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();

    public static <T, R> String parseToProperty(SFunction<T, R> func) {
        return PropertyNamer.methodToProperty(parse(func).getImplMethodName());
    }

    public static <T, R> SerializedLambda parse(SFunction<T, R> func) {
        String className = func.getClass().getName();
        return Optional.ofNullable(FUNC_CACHE.get(className))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    SerializedLambda serializedLambda = doParse(func);
                    FUNC_CACHE.put(className, new WeakReference<>(serializedLambda));
                    return serializedLambda;
                });
    }

    private static <T extends Serializable> SerializedLambda doParse(T lambda) {
        Class<?> lambdaClass = lambda.getClass();
        if (!lambdaClass.isSynthetic())
            throw new IllegalArgumentException("The requested object is not a Java lambda");

        try {
            Method method = lambda.getClass().getDeclaredMethod(SERIALIZABLE_WRITE_REPLACE_METHOD);
            method.setAccessible(true);
            return (SerializedLambda) method.invoke(lambda);
        } catch (Exception e) {
            throw new MybatisLambdaException("resolve lambda function error", e);
        }
    }
}
