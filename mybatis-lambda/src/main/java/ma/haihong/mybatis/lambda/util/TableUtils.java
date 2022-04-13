package ma.haihong.mybatis.lambda.util;

import ma.haihong.mybatis.lambda.annotation.TableField;
import ma.haihong.mybatis.lambda.annotation.TableId;
import ma.haihong.mybatis.lambda.annotation.TableName;
import ma.haihong.mybatis.lambda.metadata.TableFieldInfo;
import ma.haihong.mybatis.lambda.metadata.TableInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author haihong.ma
 */
public class TableUtils {

    private TableUtils() {
    }

    private static final Map<Class<?>, TableInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>();

    public static TableInfo initTableInfo(Class<?> entityClass) {
        return TABLE_INFO_CACHE.computeIfAbsent(entityClass, key -> {
            List<TableFieldInfo> fieldInfos = initTableFields(entityClass);
            TableFieldInfo primaryFieldInfo = fieldInfos.stream().filter(TableFieldInfo::isPrimaryKey).findFirst().orElse(null);
            if (Objects.nonNull(primaryFieldInfo)) {
                return new TableInfo(initTableName(entityClass),
                        primaryFieldInfo.getColumnName(), primaryFieldInfo.getPropertyName(), entityClass, fieldInfos);
            }
            return new TableInfo(initTableName(entityClass), null, null, entityClass, fieldInfos);
        });
    }

    private static String initTableName(Class<?> entityClass) {
        TableName tableName = entityClass.getAnnotation(TableName.class);
        if (Objects.nonNull(tableName)) {
            Assert.isNotBlank(tableName.value(), "@TableName must defined table name in Class [%s]", entityClass.getName());
            return tableName.value();
        }
        return StringUtils.camelToUnderline(entityClass.getSimpleName());
    }

    private static List<TableFieldInfo> initTableFields(Class<?> entityClass) {
        boolean hasPrimaryKey = false;
        List<TableFieldInfo> tableFieldInfos = new ArrayList<>();
        Field[] allDeclaredFields = ReflectionUtils.getAllDeclaredFields(entityClass);
        for (Field field : allDeclaredFields) {
            String columnName = null;
            String propertyName = field.getName();
            TableId tableId = field.getAnnotation(TableId.class);
            boolean isPrimaryKey = Objects.nonNull(tableId);
            if (isPrimaryKey) {
                Assert.isTrue(!hasPrimaryKey, "@TableId can't more than one in Class [%s].", entityClass.getName());

                hasPrimaryKey = true;
                columnName = tableId.value();
            } else {
                TableField tableField = field.getAnnotation(TableField.class);
                if (Objects.nonNull(tableField)) {
                    columnName = tableField.value();
                }
            }
            if (StringUtils.isBlank(columnName)){
                columnName = StringUtils.camelToUnderline(propertyName);
            }

            tableFieldInfos.add(new TableFieldInfo(isPrimaryKey, propertyName, columnName, field.getType()));
        }
        return tableFieldInfos;
    }
}
