package ma.haihong.mybatis.lambda.mapper;

import ma.haihong.mybatis.lambda.condition.Lambda;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import static ma.haihong.mybatis.lambda.constant.ParamConstants.COLLECTION;
import static ma.haihong.mybatis.lambda.constant.ParamConstants.LAMBDA;

/**
 * @author haihong.ma
 */
public interface LambdaMapper<T> {

    T findOne(@Param(LAMBDA) Object param);

    List<T> findList(@Param(LAMBDA) Object param);

    T findById(Serializable id);

    List<T> findByIds(@Param(COLLECTION) Collection<? extends Serializable> ids);

    int count(@Param(LAMBDA) Object param);

    BigDecimal sum(@Param(LAMBDA) Object param);

    BigDecimal max(@Param(LAMBDA) Object param);

    BigDecimal min(@Param(LAMBDA) Object param);

    int insert(T entity);

    int insertList(List<T> entities);

    int updateById(T entity);

    int update(@Param(LAMBDA) Object param);

    int deleteById(Serializable id);

    int deleteByIds(@Param(COLLECTION) Collection<? extends Serializable> ids);

    int delete(@Param(LAMBDA) Object param);

    default Lambda<T> lambda() {
        return new Lambda<>(this);
    }
}
