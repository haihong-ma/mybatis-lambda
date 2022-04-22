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
    private boolean reverse;
    private String operator;
    private boolean hasParam;
    private StringBuilder paramNameBuilder;
    private int perConditionParamCount = 0;

    private final List<Object> labels;
    private final AtomicInteger paramIndex;
    private final Map<String, Object> paramMap;
    private final LambdaClassVisitor classVisitor;

    private final ReflectorFactory reflectorFactory;
    private final ObjectWrapperFactory objectWrapperFactory;

    private final static String STRING_OWNER = "java/lang/String";
    private final static List<String> NUMBER_BOXING_METHODS =
            Arrays.asList("intValue", "longValue", "floatValue", "doubleValue", "byteValue", "shortValue", "valueOf");
    private final static List<String> NULLABLE_OPERATOR = Arrays.asList(IS_NULL, IS_NOT_NULL);

    public LambdaMethodVisitor(LambdaClassVisitor classVisitor, Map<String, Object> paramMap) {
        super(Opcodes.ASM5);
        this.paramMap = paramMap;
        this.labels = new ArrayList<>();
        this.classVisitor = classVisitor;
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
            perConditionParamCount++;
            paramNameBuilder.append(DOT).append(PARAM).append(var);
        }
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        String finalOperator = operator;
        if (Objects.isNull(operator)) {
            switch (opcode) {
                case Opcodes.IFEQ:
                case Opcodes.IF_ICMPEQ:
                    finalOperator = EQUAL;
                    break;
                case Opcodes.IFNE:
                case Opcodes.IF_ICMPNE:
                    finalOperator = NOT_EQUAL;
                    break;
                case Opcodes.IFLT:
                case Opcodes.IF_ICMPLT:
                    finalOperator = LESS_THAN;
                    break;
                case Opcodes.IFLE:
                case Opcodes.IF_ICMPLE:
                    finalOperator = LESS_THAN_AND_EQUAL;
                    break;
                case Opcodes.IFGT:
                case Opcodes.IF_ICMPGT:
                    finalOperator = GREATER_THAN;
                    break;
                case Opcodes.IFGE:
                case Opcodes.IF_ICMPGE:
                    finalOperator = GREATER_THAN_AND_EQUAL;
                    break;
                case Opcodes.IF_ACMPEQ:
                case Opcodes.IFNULL:
                    finalOperator = IS_NULL;
                    break;
                case Opcodes.IF_ACMPNE:
                case Opcodes.IFNONNULL:
                    finalOperator = IS_NOT_NULL;
                    break;
            }
        } else if (Opcodes.IFEQ == opcode) {
            finalOperator = negate(operator);
        }
        String sqlSegment = Objects.nonNull(finalOperator) ? getSqlSegment(finalOperator) : null;
        labels.add(new LabelExpression(label, reverse, finalOperator, sqlSegment));
        clearVariables();
        validateCondition(finalOperator);
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
    public void visitLabel(Label label) {
        labels.add(label);
    }

    @Override
    public void visitEnd() {
        classVisitor.setSqlSegment(inferSqlSegment());
    }

    private String inferSqlSegment() {
        if (labels.isEmpty()) {
            validateCondition(operator);
            return String.format(getSqlSegment(operator), operator);
        }
        int startIndex;
        Label trueLabel;
        int size = labels.size();
        Object beforeGoto = labels.get(size - 4);
        if (beforeGoto instanceof Label) {
            startIndex = size - 5;
            trueLabel = (Label) beforeGoto;
        } else {
            trueLabel = null;
            startIndex = size - 3;
        }

        Integer labelDepth = null;
        List<Label> pendingLabels = new ArrayList<>();
        Label falseLabel = (Label) labels.get(size - 2);
        List<LabelExpression> expressions = new ArrayList<>();
        for (int i = startIndex; i >= 0; i--) {
            Object label = labels.get(i);
            if (label instanceof LabelExpression) {
                LabelExpression expression = (LabelExpression) label;
                if (expression.getLabel().equals(falseLabel)) {
                    expression.setNegation(true);
                    expression.setLogical(AND);
                } else if (expression.getLabel().equals(trueLabel)) {
                    expression.setNegation(false);
                    expression.setLogical(OR);
                } else {
                    if (pendingLabels.contains(expression.getLabel()) && labelDepth > 1) {
                        expression.setLogical(AND);
                        expression.setNegation(true);
                        expression.setLeftBracket(LEFT_BRACKET);
                    }
                }
                if (Objects.nonNull(labelDepth)) {
                    if (labelDepth == 1) {
                        expression.setRightBracket(RIGHT_BRACKET);
                    }
                    labelDepth++;
                }
                if (Objects.nonNull(expression.getOperator())) {
                    expressions.add(expression);
                }
            } else {
                labelDepth = 1;
                pendingLabels.add((Label) label);
            }
        }
        StringBuilder builder = new StringBuilder();
        for (int i = expressions.size() - 1; i >= 0; i--) {
            LabelExpression expression = expressions.get(i);
            String negateOperator = expression.isNegation() ? negate(expression.getOperator()) : expression.getOperator();
            String finalOperator = expression.isReverse() ? reverse(negateOperator) : negateOperator;
            builder.append(expression.getLeftBracket())
                    .append(String.format(expression.getSqlSegment(), finalOperator))
                    .append(expression.getRightBracket())
                    .append(SPACE).append(expression.getLogical()).append(SPACE);
        }
        builder.delete(builder.lastIndexOf(AND), builder.length());
        return builder.toString();
    }

    private void addParam(Object param) {
        perConditionParamCount++;
        if (Objects.isNull(column)) {
            reverse = true;
        }
        String paramName = PARAM + paramIndex.getAndIncrement();
        paramNameBuilder.append(DOT).append(paramName);
        paramMap.put(paramName, param);
    }

    private void clearVariables() {
        this.column = null;
        this.operator = null;
        this.reverse = false;
        this.hasParam = false;
        this.paramNameBuilder = new StringBuilder(LAMBDA_DOT_PARAM_MAP);
    }

    private boolean isBoxingMethod(String owner, String name) {
        Class<?> resultClass = ReflectionUtils.getClass(Type.getObjectType(owner).getClassName());
        return Number.class.isAssignableFrom(resultClass) && NUMBER_BOXING_METHODS.contains(name);
    }

    private String getSqlSegment(String finalOperator) {
        String segment = column + SPACE + "%s";
        if (IS_NOT_NULL.equals(finalOperator) || IS_NULL.equals(finalOperator)) {
            return segment;
        }
        String paramSegment;
        String paramName = paramNameBuilder.toString();
        if (IN.equals(finalOperator)) {
            String inSegment = IntStream.range(0, getParamListSize())
                    .mapToObj(index -> HASH_LEFT_BRACE + paramName + LEFT_SQUARE_BRACKET + index + RIGHT_SQUARE_BRACKET + RIGHT_BRACE)
                    .collect(Collectors.joining(COMMA));
            paramSegment = LEFT_BRACKET + inSegment + RIGHT_BRACKET;
        } else if (LIKE.equals(finalOperator)) {
            paramSegment = SqlScriptUtils.convertLike(paramName);
        } else {
            paramSegment = SqlScriptUtils.safeParam(paramName);
        }
        return segment + SPACE + paramSegment;
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

    private void validateCondition(String finalOperation) {
        Assert.isTrue(perConditionParamCount == 1 ||
                (perConditionParamCount == 0 && NULLABLE_OPERATOR.contains(finalOperation)), "Conditional formatting error. Must contain both an property and a parameter");
        perConditionParamCount = 0;
    }

    private String negate(String negateOperator) {
        switch (negateOperator) {
            case LESS_THAN:
                return GREATER_THAN_AND_EQUAL;
            case LESS_THAN_AND_EQUAL:
                return GREATER_THAN;
            case GREATER_THAN:
                return LESS_THAN_AND_EQUAL;
            case GREATER_THAN_AND_EQUAL:
                return LESS_THAN;
            case IS_NOT_NULL:
                return IS_NULL;
            case IS_NULL:
                return IS_NOT_NULL;
            case EQUAL:
                return NOT_EQUAL;
            case NOT_EQUAL:
                return EQUAL;
        }
        return negateOperator;
    }

    private String reverse(String reverseOperator) {
        switch (reverseOperator) {
            case LESS_THAN:
                return GREATER_THAN;
            case LESS_THAN_AND_EQUAL:
                return GREATER_THAN_AND_EQUAL;
            case GREATER_THAN:
                return LESS_THAN;
            case GREATER_THAN_AND_EQUAL:
                return LESS_THAN_AND_EQUAL;
        }
        return reverseOperator;
    }

    private static class LabelExpression {
        private String logical;
        private boolean negation;
        private String leftBracket;
        private String rightBracket;

        private final Label label;
        private final boolean reverse;
        private final String operator;
        private final String sqlSegment;

        public LabelExpression(Label label, boolean reverse, String operator, String sqlSegment) {
            this.label = label;
            this.reverse = reverse;
            this.operator = operator;
            this.sqlSegment = sqlSegment;
            this.leftBracket = this.rightBracket = EMPTY;
        }

        public void setLogical(String logical) {
            this.logical = logical;
        }

        public void setNegation(boolean negation) {
            this.negation = negation;
        }

        public void setLeftBracket(String leftBracket) {
            this.leftBracket = leftBracket;
        }

        public void setRightBracket(String rightBracket) {
            this.rightBracket = rightBracket;
        }

        public Label getLabel() {
            return label;
        }

        public boolean isReverse() {
            return reverse;
        }

        public String getLogical() {
            return logical;
        }

        public String getOperator() {
            return operator;
        }

        public boolean isNegation() {
            return negation;
        }

        public String getSqlSegment() {
            return sqlSegment;
        }

        public String getLeftBracket() {
            return leftBracket;
        }

        public String getRightBracket() {
            return rightBracket;
        }
    }
}
