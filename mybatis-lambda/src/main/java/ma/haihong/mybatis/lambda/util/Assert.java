package ma.haihong.mybatis.lambda.util;

import ma.haihong.mybatis.lambda.exception.MybatisLambdaException;

import java.util.Objects;

/**
 * @author haihong.ma
 */
public class Assert {
    private Assert() {
    }

    public static void notNull(Object obj, Throwable throwable) {
        isTrue(Objects.nonNull(obj), throwable);
    }

    public static void notNull(Object obj, String msg, Object... params) {
        isTrue(Objects.nonNull(obj), msg, params);
    }

    public static void notNull(Object obj, String msg, Throwable throwable, Object... params) {
        isTrue(Objects.nonNull(obj), msg, throwable, params);
    }

    public static void isNotBlank(String str, Throwable throwable) {
        isTrue(StringUtils.isNotBlank(str), throwable);
    }

    public static void isNotBlank(String str, String msg, Object... params) {
        isTrue(StringUtils.isNotBlank(str), msg, params);
    }

    public static void isNotBlank(String str, String msg, Throwable throwable, Object... params) {
        isTrue(StringUtils.isNotBlank(str), msg, throwable, params);
    }

    public static void isTrue(boolean expression, Throwable throwable) {
        if (!expression) {
            throw new MybatisLambdaException(throwable);
        }
    }

    public static void isTrue(boolean expression, String msg, Object... params) {
        if (!expression) {
            throw new MybatisLambdaException(String.format(msg, params));
        }
    }

    public static void isTrue(boolean expression, String msg, Throwable throwable, Object... params) {
        if (!expression) {
            throw new MybatisLambdaException(String.format(msg, params), throwable);
        }
    }
}
