package ma.haihong.mybatis.lambda.core.action;

/**
 * @author haihong.ma
 */
public interface AllAction<T> extends SelectAction<T>, UpdateAction<T>, DeleteAction<T> {
}
