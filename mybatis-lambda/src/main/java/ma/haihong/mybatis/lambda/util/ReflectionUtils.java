package ma.haihong.mybatis.lambda.util;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author haihong.ma
 */
public class ReflectionUtils {

    private static final char DOT = '.';
    private static final char SLASH = '/';
    private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
    private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentHashMap<>(256);

    private ReflectionUtils() {
    }

    public static Class<?> getGenericClass(Class<?> targetClass) {
        Type[] types = targetClass.getGenericInterfaces();
        ParameterizedType target = null;
        for (Type type : types) {
            if (type instanceof ParameterizedType) {
                Type[] typeArray = ((ParameterizedType) type).getActualTypeArguments();
                if (Objects.nonNull(typeArray) && typeArray.length > 0) {
                    for (Type t : typeArray) {
                        if (!(t instanceof TypeVariable) && !(t instanceof WildcardType)) {
                            target = (ParameterizedType) type;
                        }
                        break;
                    }
                }
                break;
            }
        }
        return target == null ? null : (Class<?>) target.getActualTypeArguments()[0];
    }

    public static String convertNameWithDOT(String nameWithSlash) {
        return nameWithSlash.replace(SLASH, DOT);
    }

    public static String convertNameWithSlash(String nameWithDot) {
        return nameWithDot.replace(DOT, SLASH);
    }

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (Throwable var3) {
            return null;
        }
    }

    public static Field[] getAllDeclaredFields(Class<?> leafClass) {
        final List<Field> fields = new ArrayList<>(32);
        doWithFields(leafClass, fields::add);
        return fields.toArray(EMPTY_FIELD_ARRAY);
    }

    public static void doWithFields(Class<?> clazz, FieldCallback fc) {
        // Keep backing up the inheritance hierarchy.
        Class<?> targetClass = clazz;
        do {
            Field[] fields = getDeclaredFields(targetClass);
            for (Field field : fields) {
                try {
                    fc.doWith(field);
                } catch (IllegalAccessException ex) {
                    throw new IllegalStateException("Not allowed to access field '" + field.getName() + "': " + ex);
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != null && targetClass != Object.class);
    }

    private static Field[] getDeclaredFields(Class<?> clazz) {
        Assert.notNull(clazz, "Class must not be null");
        Field[] result = declaredFieldsCache.get(clazz);
        if (result == null) {
            try {
                result = clazz.getDeclaredFields();
                declaredFieldsCache.put(clazz, (result.length == 0 ? EMPTY_FIELD_ARRAY : result));
            } catch (Throwable ex) {
                throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() +
                        "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
            }
        }
        return result;
    }

    /**
     * Callback interface invoked on each field in the hierarchy.
     */
    @FunctionalInterface
    public interface FieldCallback {

        /**
         * Perform an operation using the given field.
         *
         * @param field the field to operate on
         */
        void doWith(Field field) throws IllegalArgumentException, IllegalAccessException;
    }
}
