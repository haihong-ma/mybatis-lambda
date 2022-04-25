package ma.haihong.mybatis.lambda.parsing.model;

/**
 * @author haihong.ma
 */
public class PropertyInfo {
    private final String propertyName;
    private final Class<?> entityClass;

    public PropertyInfo(String propertyName, Class<?> entityClass) {
        this.propertyName = propertyName;
        this.entityClass = entityClass;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }
}
