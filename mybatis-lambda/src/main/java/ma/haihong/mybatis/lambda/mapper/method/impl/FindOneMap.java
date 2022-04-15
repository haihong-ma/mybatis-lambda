package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;

import java.util.Map;

/**
 * @author haihong.ma
 */
public class FindOneMap extends FindOne {
    public FindOneMap() {
        super(SqlTemplate.FIND_ONE_MAP);
    }

    @Override
    protected Class<?> getResultType() {
        return Map.class;
    }
}
