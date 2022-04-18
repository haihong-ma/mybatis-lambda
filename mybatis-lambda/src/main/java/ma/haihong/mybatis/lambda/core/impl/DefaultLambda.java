package ma.haihong.mybatis.lambda.core.impl;

import ma.haihong.mybatis.lambda.core.Lambda;
import ma.haihong.mybatis.lambda.mapper.LambdaMapper;
import ma.haihong.mybatis.lambda.parser.func.SFunction;
import ma.haihong.mybatis.lambda.parser.func.SPredicate;
import ma.haihong.mybatis.lambda.util.Assert;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.*;
import static ma.haihong.mybatis.lambda.constant.SqlConstants.*;

/**
 * @author haihong.ma
 */
public class DefaultLambda<T> extends DefaultFunc<T> implements Lambda<T> {

    private Object entity;

    private static final String UPDATE_ENTITY_NULL_TIP = "entity can't be null";
    private static final String UPDATE_SET_NULL_TIP = "updateSet can't be null";

    public DefaultLambda(LambdaMapper<T> mapper) {
        super(mapper);
    }

    @Override
    public int delete(SPredicate<T> where) {
        Assert.notNull(where, WHERE_PREDICATE_NULL_TIP);
        super.where(where);
        return mapper.delete(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T findOne() {
        T result = mapper.findOne(this);
        if (Objects.nonNull(result) && Objects.nonNull(selectFunc)) {
            return (T) selectFunc.apply(result);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<T> findList() {
        List<T> result = mapper.findList(this);
        if (Objects.nonNull(selectFunc)) {
            return (List<T>) result.stream().map(selectFunc).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    @SuppressWarnings("boxing")
    public long count() {
        return doAggregate(COUNT_METHOD, ONE);
    }

    @Override
    @SuppressWarnings("boxing")
    public long count(SFunction<T, ?> column) {
        return doAggregate(COUNT_METHOD, column);
    }

    @Override
    @SuppressWarnings("boxing")
    public long count(SFunction<T, ?> column, boolean distinct) {
        assertSelectFunctionNotNull(column);
        String distinctSegment = distinct ? DISTINCT + SPACE : EMPTY;
        return doAggregate(COUNT_METHOD, distinctSegment + convertToColumnName(column));
    }

    @Override
    public <R> R max(SFunction<T, R> column) {
        return doMaxOrMin(MAX_METHOD, column);
    }

    @Override
    public <R> R min(SFunction<T, R> column) {
        return doMaxOrMin(MIN_METHOD, column);
    }

    @Override
    public <R extends Number> BigDecimal sum(SFunction<T, R> column) {
        return doAggregate(SUM_METHOD, column);
    }

    @Override
    public int update(T entity, SPredicate<T> where) {
        assertWherePredicateNotNull(where);
        Assert.notNull(entity, UPDATE_ENTITY_NULL_TIP);

        this.entity = entity;
        super.where(where);
        return mapper.update(this);
    }

    @Override
    public int update(Consumer<UpdateSet<T>> updateSet, SPredicate<T> where) {
        Assert.notNull(updateSet, UPDATE_SET_NULL_TIP);
        assertWherePredicateNotNull(where);

        UpdateSet<T> set = new UpdateSet<>();
        updateSet.accept(set);
        this.entity = set.getParamMap();
        super.where(where);
        return mapper.update(this);
    }

    public Object getEntity() {
        return entity;
    }

    @SuppressWarnings("unchecked")
    private <R> R doMaxOrMin(String aggregateMethod, SFunction<T, ?> column) {
        assertSelectFunctionNotNull(column);
        String columnName = convertToColumnName(column);
        setSelectSegment(aggregateMethod + LEFT_BRACKET + columnName + RIGHT_BRACKET + SPACE + AS + SPACE + columnName);
        T result = mapper.findOne(this);
        return Objects.nonNull(result) ? (R) column.apply(result) : null;
    }

    private <R extends Number> R doAggregate(String aggregateMethod, SFunction<T, ?> column) {
        assertSelectFunctionNotNull(column);
        return doAggregate(aggregateMethod, convertToColumnName(column));
    }

    @SuppressWarnings("unchecked")
    private <R extends Number> R doAggregate(String aggregateMethod, String whatAggregate) {
        String resultAlias = "Result";
        setSelectSegment(aggregateMethod + LEFT_BRACKET + whatAggregate + RIGHT_BRACKET + SPACE + AS + SPACE + resultAlias);
        Map<String, ?> result = mapper.findOneMap(this);
        return (R) (Objects.nonNull(result) ? result.get(resultAlias) : null);
    }
}
