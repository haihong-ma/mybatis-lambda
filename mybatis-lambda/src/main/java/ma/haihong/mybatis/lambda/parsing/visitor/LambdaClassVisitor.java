package ma.haihong.mybatis.lambda.parsing.visitor;


import ma.haihong.mybatis.lambda.parsing.ParsedResult;
import ma.haihong.mybatis.lambda.util.ReflectionUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.SerializedLambda;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.SPACE;
import static ma.haihong.mybatis.lambda.constant.ParamConstants.PARAM;
import static ma.haihong.mybatis.lambda.constant.SqlConstants.AND;

/**
 * @author haihong.ma
 */
public class LambdaClassVisitor extends ClassVisitor {

    private ParsedResult parsedResult;

    private final String methodName;
    private final String methodDescriptor;
    private final SerializedLambda lambda;
    private final Class<?> entityClass;
    private final Map<String, Object> paramMap;

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
        for (int index = 0; index < getParamCount(); index++) {
            paramMap.put(PARAM + index, getParam(index));
        }
        return new LambdaMethodVisitor(this, paramMap);
    }

    public ParsedResult getParseResult() {
        return parsedResult;
    }

    void setResult(List<String> sqlSegments) {
        String whereSegment = String.join(SPACE + AND + SPACE, sqlSegments);
        parsedResult = new ParsedResult(whereSegment, paramMap);
    }

    int getParamCount() {
        return lambda.getCapturedArgCount();
    }

    boolean hasParam(int index) {
        return lambda.getCapturedArgCount() > index;
    }

    Object getParam(int index) {
        return lambda.getCapturedArg(index);
    }

    Class<?> getEntityClass() {
        return entityClass;
    }
}