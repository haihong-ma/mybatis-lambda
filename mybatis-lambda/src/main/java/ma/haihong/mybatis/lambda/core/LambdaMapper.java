package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.interfaces.Lambda;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

import static ma.haihong.mybatis.lambda.constant.ParamConstants.LAMBDA;

/**
 * @author haihong.ma
 */
interface LambdaMapper<T> {

    T findOne(@Param(LAMBDA) Lambda<T> lambda);

    List<T> findList(@Param(LAMBDA) Lambda<T> lambda);

    Map<String, ?> findOneMap(@Param(LAMBDA) Lambda<T> lambda);

    int update(@Param(LAMBDA) Lambda<T> lambda);

    int delete(@Param(LAMBDA) Lambda<T> lambda);
}
