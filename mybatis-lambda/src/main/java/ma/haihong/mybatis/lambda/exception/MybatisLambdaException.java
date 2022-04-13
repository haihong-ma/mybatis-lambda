package ma.haihong.mybatis.lambda.exception;

/**
 * @author haihong.ma
 */
public class MybatisLambdaException extends RuntimeException{

    public MybatisLambdaException() {
    }

    public MybatisLambdaException(String message) {
        super(message);
    }

    public MybatisLambdaException(String message, Throwable cause) {
        super(message, cause);
    }

    public MybatisLambdaException(Throwable cause) {
        super(cause);
    }

    public MybatisLambdaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
