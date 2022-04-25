package ma.haihong.mybatis.lambda.util;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;

/**
 * @author haihong.ma
 */
public class MybatisConfigUtils {

    private MybatisConfigUtils() {
    }

    public static void registerMapperRegistry(Configuration configuration, MapperRegistry mapperRegistry) {
        BeanUtils.setValue(configuration, "mapperRegistry", mapperRegistry);
    }
}
