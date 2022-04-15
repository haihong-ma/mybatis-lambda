package ma.haihong.mybatis.lambda.mapper.method;

/**
 * @author haihong.ma
 */
public enum SqlTemplate {

    FIND_ONE("findOne", "<script>\nSELECT %s FROM %s %s %s %s %s\n</script>", "通过Lambda条件查询单条数据"),
    FIND_LIST("findList", "<script>\nSELECT %s FROM %s %s %s %s %s\n</script>", "通过Lambda条件查询多条数据"),
    FIND_ONE_MAP("findOneMap", "<script>\nSELECT %s FROM %s %s %s %s %s\n</script>", "通过Lambda条件查询单条数据"),
    FIND_BY_ID("findById", "<script>\nSELECT %s FROM %s WHERE %s=#{%s}\n</script>", "通过id查单条数据"),
    FIND_BY_IDS("findByIds", "<script>\nSELECT %s FROM %s WHERE %s IN (%s)\n</script>", "通过id列表查多条数据"),

    INSERT("insert", "<script>\nINSERT INTO %s %s VALUES %s\n</script>", "插入单条数据"),
    INSERT_LIST("insertList", "<script>\nINSERT INTO %s %s VALUES %s\n</script>", "插入多条数据"),

    UPDATE_BY_ID("updateById", "<script>\nUPDATE %s SET %s WHERE %s=#{%s}\n</script>", "通过id更新"),
    UPDATE("update", "<script>\nUPDATE %s SET %s WHERE %s\n</script>", "通过Lambda条件更新"),

    DELETE_BY_ID("deleteById", "<script>\nDELETE FROM %s WHERE %s=#{%s}\n</script>", "通过id删除"),
    DELETE_BY_IDS("deleteByIds", "<script>\nDELETE FROM %s WHERE %s IN (%s)\n</script>", "通过id列表批量删除"),
    DELETE("delete", "<script>\nDELETE FROM %s WHERE %s\n</script>", "通过Lambda条件条件删除");

    private final String method;
    private final String sqlScript;

    SqlTemplate(String method, String sqlScript, String desc) {
        this.method = method;
        this.sqlScript = sqlScript;
    }

    public String getMethod() {
        return method;
    }

    public String getSqlScript() {
        return sqlScript;
    }
}
