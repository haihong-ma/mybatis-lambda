package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.mapper.BaseMapper;
import ma.haihong.mybatis.lambda.mapper.method.AbstractMethod;
import ma.haihong.mybatis.lambda.mapper.method.impl.*;
import ma.haihong.mybatis.lambda.metadata.TableInfo;
import ma.haihong.mybatis.lambda.util.ReflectionUtils;
import ma.haihong.mybatis.lambda.util.TableUtils;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.util.*;

/**
 * 因需要使用扩展的{@link MybatisMapperProxyFactory}Mapper代理工厂类，
 * 故继承{@link MapperRegistry}类，Copy相关方法
 * 并再{@link MybatisMapperRegistry#addMapper(Class)}方法中，
 * 在官方MyBatis注册完Mapper后，调用{@link MybatisMapperRegistry#addDefaultMapperMethods(Class)}方法，
 * 添加{@link BaseMapper}及{@link LambdaMapper}类中的方法
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

    private final Configuration config;
    private final Map<Class<?>, MybatisMapperProxyFactory<?>> knownMappers = new HashMap<>();

    public MybatisMapperRegistry(Configuration config) {
        super(config);
        this.config = config;
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        final MybatisMapperProxyFactory<T> mapperProxyFactory = (MybatisMapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new BindingException("Error getting mapper instance. Cause: " + e, e);
        }
    }

    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
            }
            boolean loadCompleted = false;
            try {
                knownMappers.put(type, new MybatisMapperProxyFactory<>(type));
                // It's important that the type is added before the parser is run
                // otherwise the binding may automatically be attempted by the
                // mapper parser. If the type is already known, it won't try.
                MapperAnnotationBuilder parser = new MapperAnnotationBuilder(config, type);
                parser.parse();
                loadCompleted = true;
                addDefaultMapperMethods(type);
            } finally {
                if (!loadCompleted) {
                    knownMappers.remove(type);
                }
            }
        }
    }

    /**
     * Gets the mappers.
     *
     * @return the mappers
     * @since 3.2.2
     */
    public Collection<Class<?>> getMappers() {
        return Collections.unmodifiableCollection(knownMappers.keySet());
    }

    private <T> void addDefaultMapperMethods(Class<T> type) {
        if (BaseMapper.class.isAssignableFrom(type)) {
            Class<?> entityClass = ReflectionUtils.getGenericClass(type);
            TableInfo tableInfo = TableUtils.initTableInfo(entityClass);
            MapperBuilderAssistant assistant = new MapperBuilderAssistant(config, getResource(type));
            assistant.setCurrentNamespace(type.getName());
            DEFAULT_METHODS.forEach(method -> method.addMappedStatement(assistant, config, type, tableInfo));
        }
    }

    private String getResource(Class<?> mapperClass) {
        return ReflectionUtils.convertNameWithSlash(mapperClass.getName()) + MAPPER_RESOURCE_SUFFIX;
    }
}
