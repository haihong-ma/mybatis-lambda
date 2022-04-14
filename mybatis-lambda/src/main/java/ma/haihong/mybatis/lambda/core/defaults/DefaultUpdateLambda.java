package ma.haihong.mybatis.lambda.core.defaults;

import ma.haihong.mybatis.lambda.core.UpdateLambda;
import ma.haihong.mybatis.lambda.mapper.LambdaMapper;
import ma.haihong.mybatis.lambda.parser.func.SPredicate;
import ma.haihong.mybatis.lambda.util.Assert;

import java.util.function.Consumer;

/**
 * @author haihong.ma
 */
public class DefaultUpdateLambda<T> extends DefaultWhereLambda<T> implements UpdateLambda<T> {

    private Object entity;

    public DefaultUpdateLambda(LambdaMapper<T> mapper) {
        super(mapper);
    }

    public int update(T entity, SPredicate<T> predicate) {
        Assert.notNull(entity,"entity can't be null");

        this.entity = entity;
        super.where(predicate);
        return mapper.update(this);
    }

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
