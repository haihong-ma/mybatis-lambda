package ma.haihong.mybatis.lambda.mapper;

import ma.haihong.mybatis.lambda.core.DefaultLambda;
import ma.haihong.mybatis.lambda.interfaces.Lambda;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static ma.haihong.mybatis.lambda.constant.ParamConstants.COLLECTION;

/**
 * @author haihong.ma
 */
public interface BaseMapper<T> extends Mapper<T>{

    T findById(Serializable id);

    List<T> findByIds(@Param(COLLECTION) Collection<? extends Serializable> ids);

    int insert(T entity);

    int insertList(List<T> entities);

    int updateById(T entity);

    int deleteById(Serializable id);

    int deleteByIds(@Param(COLLECTION) Collection<? extends Serializable> ids);

    default Lambda<T> lambda() {
        return new DefaultLambda<>(this);
    }
}
