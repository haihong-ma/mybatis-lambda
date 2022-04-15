package ma.haihong.mybatis.lambda;

import ma.haihong.mybatis.lambda.mapper.LambdaMapper;
import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.impl.*;
import ma.haihong.mybatis.lambda.metadata.TableInfo;
import ma.haihong.mybatis.lambda.util.ReflectionUtils;
import ma.haihong.mybatis.lambda.util.TableUtils;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * 重写MapperRegistry的{@link MapperRegistry#addMapper(Class)}方法
 * 在官方MyBatis注册完Mapper后，添加{@link LambdaMapper}类中的方法
 *
 * @author haihong.ma
 */
public class MybatisMapperRegistry extends MapperRegistry {

    private final static String MAPPER_RESOURCE_SUFFIX = ".java (best guess)";

    private final static List<AbstractMethod> DEFAULT_METHODS = Arrays.asList(
            new FindOne(), new FindList(), new FindOneMap(), new FindById(), new FindByIds(),
            new Insert(), new InsertList(),
            new Update(), new UpdateById(),
            new Delete(), new DeleteById(), new DeleteByIds());

    private final Configuration configuration;

    public MybatisMapperRegistry(Configuration config) {
        super(config);
        this.configuration = config;
    }

    @Override
    public <T> void addMapper(Class<T> mapperClass) {
        super.addMapper(mapperClass);
        if (super.hasMapper(mapperClass)) {
            Class<?> entityClass = ReflectionUtils.getGenericClass(mapperClass);
            TableInfo tableInfo = TableUtils.initTableInfo(entityClass);
            MapperBuilderAssistant assistant = new MapperBuilderAssistant(configuration, getResource(mapperClass));
            assistant.setCurrentNamespace(mapperClass.getName());
            DEFAULT_METHODS.forEach(method -> method.addMappedStatement(assistant, configuration, mapperClass, tableInfo));
        }
    }

    private String getResource(Class<?> mapperClass) {
        return ReflectionUtils.convertNameWithSlash(mapperClass.getName()) + MAPPER_RESOURCE_SUFFIX;
    }
}
