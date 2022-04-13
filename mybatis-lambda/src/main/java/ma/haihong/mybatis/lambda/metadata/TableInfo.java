package ma.haihong.mybatis.lambda.metadata;

import ma.haihong.mybatis.lambda.util.SqlScriptUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static ma.haihong.mybatis.lambda.constant.CommonConstants.*;

/**
 * @author haihong.ma
 */
public class TableInfo {

    private final String tableName;
    private final String keyColumn;
    private final String keyProperty;
    private final Class<?> entityClass;
    private final List<TableFieldInfo> fieldInfos;

    public TableInfo(String tableName, String keyColumn, String keyProperty,
                     Class<?> entityClass, List<TableFieldInfo> fieldInfos) {
        this.tableName = tableName;
        this.keyColumn = keyColumn;
        this.keyProperty = keyProperty;
        this.entityClass = entityClass;
        this.fieldInfos = fieldInfos;
    }

    public String getTableName() {
        return tableName;
    }

    public String getKeyColumn() {
        return keyColumn;
    }

    public String getKeyProperty() {
        return keyProperty;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public List<TableFieldInfo> getFieldInfos() {
        return fieldInfos;
    }

    public String getAllColumnSqlSegment() {
        return fieldInfos.stream().map(TableFieldInfo::getColumnName).collect(Collectors.joining(COMMA));
    }

    public String getInsertPropertySqlSegment(final String prefix) {
        String realPrefix = Objects.isNull(prefix) ? EMPTY : (prefix + DOT);
        return fieldInfos.stream().map(m -> SqlScriptUtils.safeParam(realPrefix + m.getPropertyName())).collect(Collectors.joining(COMMA));
    }

    public String getSetSqlSegment() {
        return SqlScriptUtils.convertTrim(fieldInfos.stream().map(TableFieldInfo::getSqlSet).filter(Objects::nonNull).collect(joining(NEWLINE)),
                null, null, null, COMMA);
    }
}
