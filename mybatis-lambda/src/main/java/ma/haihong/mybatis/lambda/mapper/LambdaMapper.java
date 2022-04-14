package ma.haihong.mybatis.lambda.mapper;

import ma.haihong.mybatis.lambda.core.Lambda;
import ma.haihong.mybatis.lambda.core.UpdateLambda;
import ma.haihong.mybatis.lambda.core.WhereLambda;
import ma.haihong.mybatis.lambda.core.defaults.DefaultLambda;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static ma.haihong.mybatis.lambda.constant.ParamConstants.COLLECTION;
import static ma.haihong.mybatis.lambda.constant.ParamConstants.LAMBDA;

/**
 * @author haihong.ma
 */
public interface LambdaMapper<T> {

    T findById(Serializable id);

    List<T> findByIds(@Param(COLLECTION) Collection<? extends Serializable> ids);

    int insert(T entity);

    int insertList(List<T> entities);

    int updateById(T entity);

    int deleteById(Serializable id);

    int deleteByIds(@Param(COLLECTION) Collection<? extends Serializable> ids);

    /**
     * 以下使用Lambda表达式参数的方法不可直接调用
     * 必须通过{@link LambdaMapper#lambda()}方法调用
     */

    T findOne(@Param(LAMBDA) Lambda<T> lambda);

    List<T> findList(@Param(LAMBDA) Lambda<T> lambda);

    int update(@Param(LAMBDA) UpdateLambda<T> lambda);

    int delete(@Param(LAMBDA) WhereLambda<T> lambda);

    default Lambda<T> lambda() {
        return new DefaultLambda<>(this);
    }
}
