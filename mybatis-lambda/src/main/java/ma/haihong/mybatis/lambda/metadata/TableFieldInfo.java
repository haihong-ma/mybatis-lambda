package ma.haihong.mybatis.lambda.metadata;

import ma.haihong.mybatis.lambda.util.SqlScriptUtils;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.*;

/**
 * @author haihong.ma
 */
public class TableFieldInfo {

    private final boolean primaryKey;
    private final String propertyName;
    private final String columnName;
    private final Class<?> propertyType;

    public TableFieldInfo(boolean primaryKey, String propertyName, String columnName, Class<?> propertyType) {
        this.primaryKey = primaryKey;
        this.propertyName = propertyName;
        this.columnName = columnName;
        this.propertyType = propertyType;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getColumnName() {
        return columnName;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public String getSqlSet() {
        if (isPrimaryKey()) {
            //主键默认不做update
            return EMPTY;
        }
        String setSqlSegment = columnName + EQUALS + HASH_LEFT_BRACE + propertyName + RIGHT_BRACE + COMMA;
        if (propertyType.isPrimitive()) {
            return setSqlSegment;
        }
        String nullableSqlSegment = propertyType.equals(String.class)
                ? SqlScriptUtils.stringNullableSqlSegment(propertyName) : SqlScriptUtils.objectNullableSqlSegment(propertyName);
        return SqlScriptUtils.convertIf(setSqlSegment, nullableSqlSegment, false);
    }
}
