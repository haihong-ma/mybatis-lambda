package ma.haihong.mybatis.lambda.core.action;

/**
 * @author haihong.ma
 */
public interface AggregateAction<T> {

    int count();

    Object max();

    Object min();

    Object sum();
}
