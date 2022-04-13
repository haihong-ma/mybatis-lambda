package ma.haihong.mybatis.lambda.condition;

import ma.haihong.mybatis.lambda.mapper.LambdaMapper;
import ma.haihong.mybatis.lambda.parser.SerializablePredicate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author haihong.ma
 */
public class Lambda<T> {
    private final LambdaMapper<?> mapper;
    private final List<SerializablePredicate<T>> wheres;

    public Lambda(LambdaMapper<T> mapper) {
        this.mapper = mapper;
        this.wheres = new ArrayList<>();
    }

    public Lambda<T> where(SerializablePredicate<T> where) {
        if (where == null) {
            throw new RuntimeException("where predicate params can not be null");
        }
        wheres.add(where);
        return this;
    }

    @SuppressWarnings("unchecked")
    public T findOne() {
        return (T) mapper.findOne(this);
    }
}
