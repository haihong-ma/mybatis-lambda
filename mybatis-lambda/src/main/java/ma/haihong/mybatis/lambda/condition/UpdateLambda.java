package ma.haihong.mybatis.lambda.condition;

import ma.haihong.mybatis.lambda.mapper.LambdaMapper;
import ma.haihong.mybatis.lambda.parser.func.SPredicate;
import ma.haihong.mybatis.lambda.util.Assert;

import java.util.function.Consumer;

/**
 * @author haihong.ma
 */
public class UpdateLambda<T> extends WhereLambda<T> {

    private Object entity;

    public UpdateLambda(LambdaMapper<T> mapper) {
        super(mapper);
    }

    public int update(T entity, SPredicate<T> predicate) {
        Assert.notNull(entity,"entity can't be null");

        this.entity = entity;
        return mapper.update(this);
    }

    public int update(Consumer<UpdateSet<T>> updateSet, SPredicate<T> predicate) {
        Assert.notNull(updateSet,"updateSet can't be null");

        UpdateSet<T> set = new UpdateSet<>();
        updateSet.accept(set);
        this.entity = set.getParamMap();
        return mapper.update(this);
    }

    public Object getEntity() {
        return entity;
    }


}
