package ma.haihong.mybatis.lambda.mapper.method.impl;

import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.SqlTemplate;
import ma.haihong.mybatis.lambda.util.SqlScriptUtils;
import ma.haihong.mybatis.lambda.util.StringUtils;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;

/**
 * @author haihong.ma
 */
public class Insert extends AbstractMethod {

    public Insert() {
        super(SqlTemplate.INSERT);
    }

    public Insert(SqlTemplate sqlTemplate) {
        super(sqlTemplate);
    }

    @Override
    protected void doAddMappedStatement() {
        KeyGenerator keyGenerator = StringUtils.isBlank(tableInfo.getKeyProperty())
                ? new NoKeyGenerator() : new Jdbc3KeyGenerator();
        addInsertMappedStatement(keyGenerator);
    }

    @Override
    protected String initSqlScript() {
        String columnSegment = SqlScriptUtils.withBracket(tableInfo.getAllColumnSqlSegment());
        String valueSegment = SqlScriptUtils.withBracket(tableInfo.getInsertPropertySqlSegment(null));
        return String.format(sqlTemplate.getSqlScript(), tableInfo.getTableName(), columnSegment, valueSegment);
    }

    @Override
    protected Class<?> getParameterType() {
        return tableInfo.getEntityClass();
    }
}
