package ma.haihong.mybatis.lambda.parsing.visitor;


import ma.haihong.mybatis.lambda.parsing.model.ParsedCache;
import ma.haihong.mybatis.lambda.util.ReflectionUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.SerializedLambda;
import java.util.HashMap;
import java.util.Map;

/**
 * @author haihong.ma
 */
public class LambdaClassVisitor extends ClassVisitor {

    private ParsedCache parsedCache;

    private final String methodName;
    private final String methodDescriptor;
    private final SerializedLambda lambda;
    private final Class<?> entityClass;
    private final HashMap<String, Object> paramMap;

    public LambdaClassVisitor(SerializedLambda lambda) {
        super(Opcodes.ASM5);
        this.lambda = lambda;
        this.paramMap = new HashMap<>();
        this.methodName = lambda.getImplMethodName();
        this.methodDescriptor = lambda.getImplMethodSignature();
        this.entityClass = ReflectionUtils.getClass(Type.getArgumentTypes(lambda.getInstantiatedMethodType())[0].getClassName());
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (!(methodName.equals(name) && methodDescriptor.equals(descriptor))) {
            return null;
        }
        return new LambdaMethodVisitor(this, paramMap);
    }

    public ParsedCache getParsedCache() {
        return parsedCache;
    }

    void setParsedCache(ParsedCache parsedCache) {
        this.parsedCache = parsedCache;
    }

    int getCapturedArgCount() {
        return lambda.getCapturedArgCount();
    }

    boolean hasCapturedArg(int index) {
        return lambda.getCapturedArgCount() > index;
    }

    boolean hasCapturedArg(){
        return hasCapturedArg(0);
    }

    Class<?> getEntityClass() {
        return entityClass;
    }
}
