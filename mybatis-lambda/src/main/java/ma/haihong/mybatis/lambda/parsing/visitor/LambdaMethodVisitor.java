package ma.haihong.mybatis.lambda.parsing.visitor;

import ma.haihong.mybatis.lambda.exception.MybatisLambdaException;
import ma.haihong.mybatis.lambda.util.Assert;
import ma.haihong.mybatis.lambda.util.ReflectionUtils;
import ma.haihong.mybatis.lambda.util.SqlScriptUtils;
import ma.haihong.mybatis.lambda.util.TableUtils;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.*;
import static ma.haihong.mybatis.lambda.constant.ParamConstants.LAMBDA_DOT_PARAM_MAP;
import static ma.haihong.mybatis.lambda.constant.ParamConstants.PARAM;
import static ma.haihong.mybatis.lambda.constant.SqlConstants.*;

/**
 * @author haihong.ma
 */
public class LambdaMethodVisitor extends MethodVisitor {

    private String column;
    private String operator;
    private boolean hasParam;
    private StringBuilder paramNameBuilder;

    private final List<String> sqlSegments;
    private final AtomicInteger paramIndex;
    private final Map<String, Object> paramMap;
    private final LambdaClassVisitor classVisitor;

    private final ReflectorFactory reflectorFactory;
    private final ObjectWrapperFactory objectWrapperFactory;

    private final static String STRING_OWNER = "java/lang/String";
    private final static List<String> NUMBER_BOXING_METHODS =
            Arrays.asList("intValue", "longValue", "floatValue", "doubleValue", "byteValue", "shortValue", "valueOf");

    public LambdaMethodVisitor(LambdaClassVisitor classVisitor, Map<String, Object> paramMap) {
        super(Opcodes.ASM5);
        this.paramMap = paramMap;
        this.classVisitor = classVisitor;
        this.sqlSegments = new ArrayList<>();
        this.paramNameBuilder = new StringBuilder(LAMBDA_DOT_PARAM_MAP);
        this.paramIndex = new AtomicInteger(classVisitor.getParamCount());

        this.reflectorFactory = new DefaultReflectorFactory();
        this.objectWrapperFactory = new DefaultObjectWrapperFactory();
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (isBoxingMethod(owner, name)) {
            return;
        }
        if (hasParam) {
            if (EQUALS_METHOD.equals(name)) {
                operator = EQUAL;
                return;
            }
            if (CONTAINS_METHOD.equals(name)) {
                if (STRING_OWNER.equals(owner)) {
                    operator = LIKE;
                } else {
                    operator = IN;
                }
                return;
            }
            paramNameBuilder.append(DOT).append(PropertyNamer.methodToProperty(name));
        } else {
            if (name.startsWith(GET_METHOD_PREFIX)) {
                column = TableUtils.propertyToColumn(classVisitor.getEntityClass(), PropertyNamer.methodToProperty(name));
            } else if (EQUALS_METHOD.equals(name)) {
                Assert.notNull(column, "only support entity column use equals method");
                operator = EQUAL;
            } else if (CONTAINS_METHOD.equals(name)) {
                Assert.notNull(column, "only support entity column use contains method");
                if (STRING_OWNER.equals(owner)) {
                    operator = LIKE;
                } else {
                    operator = IN;
                }
            } else {
                throw new MybatisLambdaException("entity or entity column method [" + name + "] nonsupport");
            }
        }
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        hasParam = classVisitor.hasParam(var);
        if (hasParam) {
            paramNameBuilder.append(DOT).append(PARAM).append(var);
        }
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        String realOperator = operator;
        if (Objects.isNull(operator)) {
            switch (opcode) {
                case Opcodes.IFNE:
                case Opcodes.IF_ICMPEQ:
                    realOperator = EQUAL;
                    break;
                case Opcodes.IFEQ:
                case Opcodes.IF_ICMPNE:
                    realOperator = NOT_EQUAL;
                    break;
                case Opcodes.IFGE:
                case Opcodes.IF_ICMPLT:
                    realOperator = LESS_THAN;
                    break;
                case Opcodes.IFGT:
                case Opcodes.IF_ICMPLE:
                    realOperator = LESS_THAN_AND_EQUAL;
                    break;
                case Opcodes.IFLE:
                case Opcodes.IF_ICMPGT:
                    realOperator = GREATER_THAN;
                    break;
                case Opcodes.IFLT:
                case Opcodes.IF_ICMPGE:
                    realOperator = GREATER_THAN_AND_EQUAL;
                    break;
                case Opcodes.IF_ACMPEQ:
                case Opcodes.IFNULL:
                    sqlSegments.add(column + SPACE + IS_NOT_NULL);
                    break;
                case Opcodes.IF_ACMPNE:
                case Opcodes.IFNONNULL:
                    sqlSegments.add(column + SPACE + IS_NULL);
                    break;
            }
        }
        if (Objects.nonNull(realOperator)) {
            String paramSegment;
            String paramName = paramNameBuilder.toString();
            if (IN.equals(realOperator)) {
                String inSegment = IntStream.range(0, getParamListSize())
                        .mapToObj(index -> HASH_LEFT_BRACE + paramName + LEFT_SQUARE_BRACKET + index + RIGHT_SQUARE_BRACKET + RIGHT_BRACE)
                        .collect(Collectors.joining(COMMA));
                paramSegment = LEFT_BRACKET + inSegment + RIGHT_BRACKET;
            } else if (LIKE.equals(realOperator)) {
                paramSegment = SqlScriptUtils.convertLike(paramName);
            } else {
                paramSegment = SqlScriptUtils.safeParam(paramName);
            }
            sqlSegments.add(column + SPACE + realOperator + SPACE + paramSegment);
        }
        clearVariables();
    }

    @Override
    public void visitInsn(int opcode) {
        switch (opcode) {
            case Opcodes.FCONST_0:
                addParam(0f);
                break;
            case Opcodes.FCONST_1:
                addParam(1f);
                break;
            case Opcodes.FCONST_2:
                addParam(2f);
                break;
            case Opcodes.ICONST_M1:
                addParam(-1);
                break;
            case Opcodes.ICONST_0:
                addParam(0);
                break;
            case Opcodes.ICONST_1:
                addParam(1);
                break;
            case Opcodes.ICONST_2:
                addParam(2);
                break;
            case Opcodes.ICONST_3:
                addParam(3);
                break;
            case Opcodes.ICONST_4:
                addParam(4);
                break;
            case Opcodes.ICONST_5:
                addParam(5);
                break;
            case Opcodes.LCONST_0:
                addParam(0L);
                break;
            case Opcodes.LCONST_1:
                addParam(1L);
                break;
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        addParam(operand);
    }

    @Override
    public void visitLdcInsn(Object value) {
        addParam(value);
    }

    @Override
    public void visitEnd() {
        classVisitor.setResult(sqlSegments);
    }

    private void addParam(Object param) {
        String paramName = PARAM + paramIndex.getAndIncrement();
        paramNameBuilder.append(DOT).append(paramName);
        paramMap.put(paramName, param);
    }

    private void clearVariables() {
        this.column = null;
        this.operator = null;
        this.hasParam = false;
        this.paramNameBuilder = new StringBuilder(LAMBDA_DOT_PARAM_MAP);
    }

    private boolean isBoxingMethod(String owner, String name) {
        Class<?> resultClass = ReflectionUtils.getClass(Type.getObjectType(owner).getClassName());
        return Number.class.isAssignableFrom(resultClass) && NUMBER_BOXING_METHODS.contains(name);
    }

    private int getParamListSize() {
        Object paramValue = paramMap;
        String paramName = paramNameBuilder.toString().replace(LAMBDA_DOT_PARAM_MAP + DOT, EMPTY);
        for (String item : paramName.split("\\.")) {
            MetaObject metaObject = MetaObject.forObject(paramValue, null, objectWrapperFactory, reflectorFactory);
            paramValue = metaObject.getValue(item);
        }
        if (paramValue instanceof Collection) {
            return ((Collection<?>) paramValue).size();
        }
        throw new MybatisLambdaException("param type [" + paramValue.getClass().getName() + "] not support in operation");
    }
}
