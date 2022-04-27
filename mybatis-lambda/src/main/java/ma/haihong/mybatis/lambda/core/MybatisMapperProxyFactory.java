package ma.haihong.mybatis.lambda.core;

import ma.haihong.mybatis.lambda.mapper.BaseMapper;
import org.apache.ibatis.binding.MapperProxy;
import org.apache.ibatis.binding.MapperProxyFactory;

import java.lang.reflect.Proxy;

/**
 * 重写{@link MapperProxyFactory#newInstance(MapperProxy)}方法，添加LambdaMapper接口
 *
 * @author haihong.ma
 */
public class MybatisMapperProxyFactory<T> extends MapperProxyFactory<T> {

    private final Class<T> mapperInterface;

    public MybatisMapperProxyFactory(Class<T> mapperInterface) {
        super(mapperInterface);
        this.mapperInterface = mapperInterface;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy<T> mapperProxy) {
        Class<?>[] interfaces = BaseMapper.class.isAssignableFrom(mapperInterface)
                ? new Class[]{mapperInterface, LambdaMapper.class} : new Class[]{mapperInterface};
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), interfaces, mapperProxy);
    }
}
