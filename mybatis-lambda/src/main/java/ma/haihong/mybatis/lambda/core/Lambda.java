package ma.haihong.mybatis.lambda.core;

/**
 * @author haihong.ma
 */
public interface Lambda<T> extends UpdateLambda<T>, WhereLambda<T>, DeleteLambda<T> {
}
