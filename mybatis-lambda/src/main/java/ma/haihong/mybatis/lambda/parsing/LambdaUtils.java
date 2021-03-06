package ma.haihong.mybatis.lambda.parsing;

import ma.haihong.mybatis.lambda.exception.MybatisLambdaException;
import ma.haihong.mybatis.lambda.parsing.func.SFunction;
import ma.haihong.mybatis.lambda.parsing.func.SPredicate;
import ma.haihong.mybatis.lambda.parsing.model.ParsedCache;
import ma.haihong.mybatis.lambda.parsing.model.ParsedResult;
import ma.haihong.mybatis.lambda.parsing.model.PropertyInfo;
import ma.haihong.mybatis.lambda.parsing.visitor.LambdaClassVisitor;
import ma.haihong.mybatis.lambda.util.Assert;
import ma.haihong.mybatis.lambda.util.BeanUtils;
import ma.haihong.mybatis.lambda.util.ReflectionUtils;
import ma.haihong.mybatis.lambda.util.SqlScriptUtils;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.objectweb.asm.ClassReader;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.*;
import static ma.haihong.mybatis.lambda.constant.ParamConstants.LAMBDA_DOT_PARAM_MAP;
import static ma.haihong.mybatis.lambda.constant.ParamConstants.PARAM;

/**
 * @author haihong.ma
 */
public class LambdaUtils {

    private static final String SERIALIZABLE_WRITE_REPLACE_METHOD = "writeReplace";
    private static final Map<String, PropertyInfo> FUNC_PARSED_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, ParsedCache> PREDICATE_PARSED_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, WeakReference<ClassReader>> CLASS_READER_CACHE = new ConcurrentHashMap<>();

    public static <T, R> PropertyInfo parse(SFunction<T, R> func) {
        return FUNC_PARSED_CACHE.computeIfAbsent(func.getClass().getName(), className -> {
            SerializedLambda lambda = doParse(func);
            String propertyName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
            Class<?> entityClass = ReflectionUtils.getClass(ReflectionUtils.convertNameWithDOT(lambda.getImplClass()));
            Assert.notNull(entityClass, "entity class resolve failed");
            return new PropertyInfo(propertyName, entityClass);
        });
    }

    public static <T> ParsedResult parse(SPredicate<T> predicate) {
        LambdaWrapper lambdaWrapper = new LambdaWrapper();
        ParsedCache parsedCache = PREDICATE_PARSED_CACHE.computeIfAbsent(predicate.getClass().getName(), (className) -> {
            SerializedLambda lambda = doParse(predicate);
            lambdaWrapper.setLambda(lambda);
            LambdaClassVisitor visitor = new LambdaClassVisitor(lambda);
            initClassReader(lambda, predicate.getClass().getClassLoader()).accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return visitor.getParsedCache();
        });
        Map<String, Object> cloneParamMap = parsedCache.getCloneParamMap();
        if (parsedCache.hasCapturedArg()) {
            AddCapturedArg(predicate, cloneParamMap, lambdaWrapper);
        }
        return new ParsedResult(buildSqlSegment(parsedCache.getParamSqlSegmentsMap(), cloneParamMap), cloneParamMap);
    }

    private static <T extends Serializable> SerializedLambda doParse(T lambda) {
        Class<?> lambdaClass = lambda.getClass();
        if (!lambdaClass.isSynthetic())
            throw new IllegalArgumentException("The requested object is not a Java lambda");

        try {
            Method method = lambda.getClass().getDeclaredMethod(SERIALIZABLE_WRITE_REPLACE_METHOD);
            method.setAccessible(true);
            return (SerializedLambda) method.invoke(lambda);
        } catch (Exception e) {
            throw new MybatisLambdaException("resolve lambda function error", e);
        }
    }

    private static ClassReader initClassReader(SerializedLambda lambda, ClassLoader classLoader) {
        String classFilePath = lambda.getImplClass() + ".class";
        return Optional.ofNullable(CLASS_READER_CACHE.get(classFilePath))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    InputStream classInputStream = classLoader.getResourceAsStream(classFilePath);
                    Assert.notNull(classInputStream, "can't read class file");
                    ClassReader classReader;
                    try {
                        classReader = new ClassReader(classInputStream);
                    } catch (Exception e) {
                        throw new MybatisLambdaException("read class file [" + classFilePath + "] error", e);
                    }
                    CLASS_READER_CACHE.put(classFilePath, new WeakReference<>(classReader));
                    return classReader;
                });
    }

    private static void AddCapturedArg(SPredicate<?> predicate, Map<String, Object> paramMap, LambdaWrapper lambdaWrapper) {
        SerializedLambda lambda = lambdaWrapper.getLambda();
        if (Objects.isNull(lambda)) {
            lambda = doParse(predicate);
        }
        for (int index = 0; index < lambda.getCapturedArgCount(); index++) {
            paramMap.put(PARAM + index, lambda.getCapturedArg(index));
        }
    }

    private static String buildSqlSegment(Map<String, String> sqlSegmentMap, Map<String, Object> paramMap) {
        StringBuilder sqlSegmentBuilder = new StringBuilder();
        sqlSegmentMap.forEach((paramName, sqlSegment) -> {
            sqlSegmentBuilder.append(sqlSegment);
            if (Objects.nonNull(paramName)) {
                String realParamName = paramName;
                CollectionInfo collectionInfo = getCollectionInfo(paramMap, paramName);
                if (collectionInfo.needOptimizeParam()) {
                    String paramKey = PARAM + paramMap.size();
                    realParamName = LAMBDA_DOT_PARAM_MAP + DOT + paramKey;
                    paramMap.put(paramKey, collectionInfo.paramValue);
                }
                sqlSegmentBuilder.append(SqlScriptUtils.convertIn(realParamName, collectionInfo.getSize()));
            }
        });
        return sqlSegmentBuilder.toString();
    }

    /**
     * ??????????????????Collection???????????????MetaObject?????????????????????????????????size???????????????IN???????????????
     */
    private static CollectionInfo getCollectionInfo(Map<String, Object> paramMap, String paramName) {
        Object paramValue = paramMap;
        String[] paramNames = paramName.replace(LAMBDA_DOT_PARAM_MAP + DOT, EMPTY).split(REGEX_DOT);
        for (String item : paramNames) {
            paramValue = BeanUtils.getValue(paramValue, item);
        }
        if (paramValue instanceof Collection) {
            return new CollectionInfo(((Collection<?>) paramValue), paramNames.length > 1);
        }
        throw new MybatisLambdaException("param type [" + paramValue.getClass().getName() + "] not support in operation");
    }

    private static class LambdaWrapper {
        private SerializedLambda lambda;

        public void setLambda(SerializedLambda lambda) {
            this.lambda = lambda;
        }

        public SerializedLambda getLambda() {
            return lambda;
        }
    }

    private static class CollectionInfo {

        private final Collection<?> paramValue;
        /**
         * ????????????????????????
         * ??????????????????????????????getXXX????????????????????????mybatis???????????????????????????????????????
         */
        private final boolean needOptimizeParam;

        public CollectionInfo(Collection<?> paramValue, boolean needOptimizeParam) {
            this.paramValue = paramValue;
            this.needOptimizeParam = needOptimizeParam;
        }

        public int getSize() {
            return paramValue.size();
        }

        public Collection<?> getParamValue() {
            return paramValue;
        }

        public boolean needOptimizeParam() {
            return needOptimizeParam;
        }
    }
}
