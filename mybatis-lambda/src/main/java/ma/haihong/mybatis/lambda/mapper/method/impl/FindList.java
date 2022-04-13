package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;

/**
 * @author haihong.ma
 */
public class FindList extends FindOne {
    public FindList() {
        super(SqlTemplate.FIND_LIST);
    }
}
