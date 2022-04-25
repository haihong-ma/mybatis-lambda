package ma.haihong.mybatis.lambda.util;

import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

/**
 * @author haihong.ma
 */
public class BeanUtils {


    private final static ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();
    private final static ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();

    private BeanUtils() {
    }

    public static Object getValue(Object obj, String property) {
        MetaObject metaObject = MetaObject.forObject(obj, null, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        return metaObject.getValue(property);
    }

    public static void setValue(Object obj, String property, Object value) {
        MetaObject metaObject = MetaObject.forObject(obj, null, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        metaObject.setValue(property, value);
    }
}
