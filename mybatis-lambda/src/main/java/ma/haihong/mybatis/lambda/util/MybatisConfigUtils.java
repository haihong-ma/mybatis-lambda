package ma.haihong.mybatis.lambda.util;

import ma.haihong.mybatis.lambda.exception.MybatisLambdaException;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;

/**
 * @author haihong.ma
 */
public class MybatisConfigUtils {

    private MybatisConfigUtils() {
    }

    public static void registerMapperRegistry(Configuration configuration, MapperRegistry mapperRegistry) {
        try {
            Class<?> configClass = configuration.getClass();
            Field mapperRegistryField = configClass.getDeclaredField("mapperRegistry");
            mapperRegistryField.setAccessible(true);
            mapperRegistryField.set(configuration, mapperRegistry);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new MybatisLambdaException("Inject customer mapper registry failed");
        }
    }
}
