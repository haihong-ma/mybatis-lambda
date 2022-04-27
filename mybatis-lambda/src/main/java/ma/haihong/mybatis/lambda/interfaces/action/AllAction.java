package ma.haihong.mybatis.lambda.interfaces.action;

/**
 * @author haihong.ma
 */
public interface AllAction<T> extends SelectAction<T>, UpdateAction<T>, DeleteAction<T>, AggregateAction<T> {
}
