package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.mapper.LambdaMapper;
import ma.haihong.mybatis.lambda.parser.func.SPredicate;
import ma.haihong.mybatis.lambda.util.Assert;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author haihong.ma
 */
public class DefaultLambda<T> extends DefaultFunction<T> implements Lambda<T> {

    private Object entity;

    public DefaultLambda(LambdaMapper<T> mapper) {
        super(mapper);
    }

    @Override
    public int delete(SPredicate<T> predicate) {
        super.where(predicate);
        return mapper.delete(this);
    }

    @Override
    public T findOne() {
        return mapper.findOne(this);
    }

    @Override
    public List<T> findList() {
        return null;
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public Object max() {
        return null;
    }

    @Override
    public Object min() {
        return null;
    }

    @Override
    public Object sum() {
        return null;
    }

    @Override
    public int update(T entity, SPredicate<T> predicate) {
        Assert.notNull(entity,"entity can't be null");

        this.entity = entity;
        super.where(predicate);
        return mapper.update(this);
    }

    @Override
    public int update(Consumer<UpdateSet<T>> updateSet, SPredicate<T> predicate) {
        Assert.notNull(updateSet,"updateSet can't be null");

        UpdateSet<T> set = new UpdateSet<>();
        updateSet.accept(set);
        this.entity = set.getParamMap();
        super.where(predicate);
        return mapper.update(this);
    }

    public Object getEntity() {
        return entity;
    }
}
