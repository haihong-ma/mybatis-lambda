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

    /**
     * 通过getXXX方法，获取到属性，并转换成列名
     */
    private String column;
    /**
     * 若属性在前，变量（或常量）在后，则为false，操作符（operator）不需反转
     * 若属性在后，变量（或常量）在前，则为ture，操作符（operator）需要反转
     */
    private boolean reverse;
    /**
     * 通过方法equals、contains解析的操作符
     */
    private String operatorFromMethod;
    /**
     * 是否有变量参数，由此判断getXXX方法解析成变量名或字段名
     */
    private boolean hasCapturedArg;
    /**
     * 参数名构造器，若参数通过如test.getXXX().getYYY()赋值，则解析param0.xxx.yyy
     */
    private StringBuilder paramNameBuilder;
    /**
     * 用来判断表达式的每个条件，是否符合一个属性对应一个变量（或常量）参数
     */
    private int perConditionParamCount = 0;
    /**
     * 标识条件表达式两边是否都为列名，用来判断是否使用equals(null)
     */
    private boolean multiColumn;

    /**
     * 调用visitJumpInsn方法或visitLabel方法对应的Label，通过调用顺利来推断实际操作符及逻辑运算符
     * 若通过{@link LambdaMethodVisitor#visitLabel(Label)}方法添加，对象为{@link Label}类型
     * 若通过{@link LambdaMethodVisitor#visitJumpInsn(int, Label)}方法添加，对象为{@link LabelExpression}类型
     */
    private final List<Object> labels;
    /**
     * 参数索引，默认值为变量参数数量+1，用来构造常量参数名
     */
    private final AtomicInteger paramIndex;
    /**
     * 所有变量或常量参数集合
     * 变量参数在{@link LambdaClassVisitor#visitMethod(int, String, String, String, String[])} ()}中初始化
     * 常量参数在visitXXX方法中添加
     */
    private final Map<String, Object> paramMap;
    private final LambdaClassVisitor classVisitor;

    private final ReflectorFactory reflectorFactory;
    private final ObjectWrapperFactory objectWrapperFactory;

    /**
     * 判断equals或contains调用者是否为字符串，由此判断使用IN或者LIKE
     */
    private final static String STRING_OWNER = "java/lang/String";
    /**
     * 装箱或拆箱方法
     */
    private final static List<String> NUMBER_BOXING_METHODS =
            Arrays.asList("intValue", "longValue", "floatValue", "doubleValue", "byteValue", "shortValue", "valueOf");
    private final static List<String> NULLABLE_OPERATOR = Arrays.asList(IS_NULL, IS_NOT_NULL);

    public LambdaMethodVisitor(LambdaClassVisitor classVisitor, Map<String, Object> paramMap) {
        super(Opcodes.ASM5);
        this.paramMap = paramMap;
        this.labels = new ArrayList<>();
        this.classVisitor = classVisitor;
        this.paramNameBuilder = new StringBuilder(LAMBDA_DOT_PARAM_MAP);
        this.paramIndex = new AtomicInteger(classVisitor.getCapturedArgCount());

        this.reflectorFactory = new DefaultReflectorFactory();
        this.objectWrapperFactory = new DefaultObjectWrapperFactory();
    }

    /**
     * 通过方法名解析
     * 若为contains或equals方法，解析为对应操作符
     * 若为普通属性，判断是否有变量参数，有则构造参数名，没有则解析成列名
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (isBoxingMethod(owner, name)) {
            return;
        }
        if (hasCapturedArg) {
            if (EQUALS_METHOD.equals(name)) {
                operatorFromMethod = EQUAL;
                return;
            }
            if (CONTAINS_METHOD.equals(name)) {
                if (STRING_OWNER.equals(owner)) {
                    operatorFromMethod = LIKE;
                } else {
                    operatorFromMethod = IN;
                }
                return;
            }
            paramNameBuilder.append(DOT).append(PropertyNamer.methodToProperty(name));
        } else {
            if (name.startsWith(GET_METHOD_PREFIX)) {
                multiColumn = Objects.nonNull(column);
                column = TableUtils.propertyToColumn(classVisitor.getEntityClass(), PropertyNamer.methodToProperty(name));
            } else if (EQUALS_METHOD.equals(name)) {
                Assert.notNull(column, "only support entity column use equals method");
                operatorFromMethod = EQUAL;
            } else if (CONTAINS_METHOD.equals(name)) {
                Assert.notNull(column, "only support entity column use contains method");
                if (STRING_OWNER.equals(owner)) {
                    operatorFromMethod = LIKE;
                } else {
                    operatorFromMethod = IN;
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

    /**
     * 通过var索引判断是否有变量参数，有则添加到参数名构造器
     */
    @Override
    public void visitVarInsn(int opcode, int var) {
        hasCapturedArg = classVisitor.hasCapturedArg(var);
        if (hasCapturedArg) {
            perConditionParamCount++;
            paramNameBuilder.append(DOT).append(PARAM).append(var);
        }
    }

    /**
     * 参数名及列名构造成功后，通过此方法解析操作符
     * 若通过equals或contains方法调用，则methodOperator不为空，若此时opcode为{@link Opcodes#IFEQ}时，因字节码编译逻辑，则需要取反，来适应普通操作符的推断逻辑
     * 若则methodOperator为空，则通过操作码判断操作符
     */
    @Override
    public void visitJumpInsn(int opcode, Label label) {
        String operator = operatorFromMethod;
        if (Objects.isNull(operatorFromMethod)) {
            switch (opcode) {
                case Opcodes.IFEQ:
                case Opcodes.IF_ICMPEQ:
                    operator = EQUAL;
                    break;
                case Opcodes.IFNE:
                case Opcodes.IF_ICMPNE:
                    operator = NOT_EQUAL;
                    break;
                case Opcodes.IFLT:
                case Opcodes.IF_ICMPLT:
                    operator = LESS_THAN;
                    break;
                case Opcodes.IFLE:
                case Opcodes.IF_ICMPLE:
                    operator = LESS_THAN_AND_EQUAL;
                    break;
                case Opcodes.IFGT:
                case Opcodes.IF_ICMPGT:
                    operator = GREATER_THAN;
                    break;
                case Opcodes.IFGE:
                case Opcodes.IF_ICMPGE:
                    operator = GREATER_THAN_AND_EQUAL;
                    break;
                case Opcodes.IF_ACMPEQ:
                case Opcodes.IFNULL:
                    operator = IS_NULL;
                    break;
                case Opcodes.IF_ACMPNE:
                case Opcodes.IFNONNULL:
                    operator = IS_NOT_NULL;
                    break;
            }
            if (Objects.nonNull(operator) && !NULLABLE_OPERATOR.contains(operator)) {
                //处理与常量0比较情况
                if (!paramMap.containsKey(paramNameBuilder.toString().replace(LAMBDA_DOT_PARAM_MAP + DOT, EMPTY))) {
                    addParam(0);
                }
            }
        } else if (Opcodes.IFEQ == opcode) {
            operator = negate(handleEqualNull(operatorFromMethod));
        } else if (Opcodes.IFNE == opcode) {
            operator = handleEqualNull(operatorFromMethod);
        }
        String sqlSegment = Objects.nonNull(operator) ? getSqlSegment(operator) : null;
        labels.add(new LabelExpression(label, reverse, operator, sqlSegment));
        clearVariables();
        validateCondition(operator);
    }

    /**
     * 添加字节码常量参数
     */
    @Override
    public void visitInsn(int opcode) {
        switch (opcode) {
            case Opcodes.DCONST_0:
                addParam(0d);
                break;
            case Opcodes.DCONST_1:
                addParam(1d);
                break;
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

    /**
     * 添加数字类型常量参数
     */
    @Override
    public void visitIntInsn(int opcode, int operand) {
        addParam(operand);
    }

    /**
     * 添加字符串类型常量参数
     */
    @Override
    public void visitLdcInsn(Object value) {
        addParam(value);
    }

    /**
     * 添加Label，用来推断逻辑运算符及实际操作符
     */
    @Override
    public void visitLabel(Label label) {
        labels.add(label);
    }

    /**
     * 解析完成，返回推断出的sql
     */
    @Override
    public void visitEnd() {
        classVisitor.setSqlSegment(inferSqlSegment());
    }

    /**
     * 推断出逻辑运算符及实际操作符，组合最终的sql
     */
    private String inferSqlSegment() {
        //Predicate只有一个条件表达式且使用equals或contains方法,则labels为空，直接获取sql返回
        if (labels.isEmpty()) {
            String finalOperator = handleEqualNull(operatorFromMethod);
            validateCondition(finalOperator);
            return String.format(getSqlSegment(finalOperator), finalOperator);
        }
        //若Predicate有多个条件表达式，则通过以下方式推断最终true或false对应的标签
        int startIndex;
        Label trueLabel;
        int size = labels.size();
        Object beforeGoto = labels.get(size - 4);
        //若倒数第四个为Label类型，则表示包含逻辑或表达式，此Label为true标签，需从倒数第5个Label推断
        if (beforeGoto instanceof Label) {
            startIndex = size - 5;
            trueLabel = (Label) beforeGoto;
        } else {//若倒数第四个非Label类型，则表示只有逻辑与表达式，没有true标签，需从倒数第3个Label推断
            trueLabel = null;
            startIndex = size - 3;
        }

        /*
         * 根据编译器编译逻辑来反向推断完整的表达式
         *  1、若当前条件表达式与下一个条件表达式为逻辑与关系，则对当前操作符取反（因为取反后如果为true，直接结束）
         *  2、若当前条件表达式与下一个条件表达式为逻辑或关系，则保持当前操作符（因为如果结果为true，直接结束）
         * 通过visit后的Label列表，从下往上推断
         *  1、若列表对象为{@link LabelExpression}类型
         *      a、判断为false标签，则表示与下一个条件表达式为逻辑与关系，且需要取反
         *      b、判断为true标签，则表示与下一个条件表达式为逻辑或关系，且不需要取反
         *      c、若非true或false标签，判断跳转的标签，与当前的LabelExpression中间存在其他LabelExpression（通过labelDepth变量判断）
         *         则表示与下一个条件表达式为与关系，需要取反，且为，添加起始符（左括号）
         *  2、若列表对象为{@link Label}类型，则表示有LabelExpression需要跳转至此，添加到pendingLabels列表，并添加结束符（右括号）
         */
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

        //通过推断后的条件表达式，构造最终的sql
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

    /**
     * 添加常量参数
     */
    private void addParam(Object param) {
        perConditionParamCount++;
        if (Objects.isNull(column)) {
            reverse = true;
        }
        String paramName = PARAM + paramIndex.getAndIncrement();
        paramNameBuilder.append(DOT).append(paramName);
        paramMap.put(paramName, param);
    }

    /**
     * 每一个条件表达式构造完成后，清除对应标识
     */
    private void clearVariables() {
        this.column = null;
        this.reverse = false;
        this.hasCapturedArg = false;
        this.operatorFromMethod = null;
        this.paramNameBuilder = new StringBuilder(LAMBDA_DOT_PARAM_MAP);
    }

    /**
     * 判断方法是否为装箱或拆箱方法
     */
    private boolean isBoxingMethod(String owner, String name) {
        Class<?> resultClass = ReflectionUtils.getClass(Type.getObjectType(owner).getClassName());
        return Number.class.isAssignableFrom(resultClass) && NUMBER_BOXING_METHODS.contains(name);
    }

    /**
     * 通过不同操作符，构造对应的sql语句
     */
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

    /**
     * 若变量参数为Collection类型，通过MetaObject及变量名路径，获取对应size，用来构造IN中的参数名
     */
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

    /**
     * 验证条件表达式是否正确，
     */
    private void validateCondition(String finalOperation) {
        Assert.isTrue(perConditionParamCount == 1 ||
                (perConditionParamCount == 0 && NULLABLE_OPERATOR.contains(finalOperation)), "Conditional formatting error. Must contain both an property and a parameter");
        perConditionParamCount = 0;
    }

    private String handleEqualNull(String operator) {
        return perConditionParamCount == 0 && !multiColumn ? IS_NULL : operator;
    }

    /**
     * 操作符取反
     */
    private String negate(String operator) {
        switch (operator) {
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
        return operator;
    }

    /**
     * 操作符反转
     */
    private String reverse(String operator) {
        switch (operator) {
            case LESS_THAN:
                return GREATER_THAN;
            case LESS_THAN_AND_EQUAL:
                return GREATER_THAN_AND_EQUAL;
            case GREATER_THAN:
                return LESS_THAN;
            case GREATER_THAN_AND_EQUAL:
                return LESS_THAN_AND_EQUAL;
        }
        return operator;
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
