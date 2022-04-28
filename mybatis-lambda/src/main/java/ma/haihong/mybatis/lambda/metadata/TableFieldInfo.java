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

    public String getSetSqlForEntity(final String prefix) {
        String prefixPropertyName = prefix + propertyName;
        String setSqlSegment = columnName + EQUAL + HASH_LEFT_BRACE + prefixPropertyName + RIGHT_BRACE + COMMA;
        if (propertyType.isPrimitive()) {
            return setSqlSegment;
        }
        return SqlScriptUtils.convertIf(setSqlSegment, SqlScriptUtils.objectNullableSqlSegment(prefixPropertyName), false);
    }

    public String getSqlSetForUpdateMap(String mapName) {
        String completeName = mapName + DOT + propertyName;
        String setSqlSegment = columnName + EQUAL + HASH_LEFT_BRACE + completeName + RIGHT_BRACE + COMMA;
        return SqlScriptUtils.convertIf(setSqlSegment, SqlScriptUtils.mapContainsKeySqlSegment(mapName, propertyName), false);
    }
}
