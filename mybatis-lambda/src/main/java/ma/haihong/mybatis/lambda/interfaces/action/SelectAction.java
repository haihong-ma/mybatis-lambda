package ma.haihong.mybatis.lambda.interfaces.action;

import java.util.List;

/**
 * @author haihong.ma
 */
public interface SelectAction<T> {

    T findOne();

    List<T> findList();

}
