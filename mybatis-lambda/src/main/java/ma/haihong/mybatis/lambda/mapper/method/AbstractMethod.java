package ma.haihong.mybatis.lambda.mapper.method;

import ma.haihong.mybatis.lambda.metadata.TableInfo;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.*;

/**
 * @author haihong.ma
 */
public abstract class AbstractMethod {
    protected static final Log logger = LogFactory.getLog(AbstractMethod.class);

    protected final SqlTemplate sqlTemplate;

    protected TableInfo tableInfo;
    protected Class<?> mapperClass;
    protected Configuration configuration;
    protected LanguageDriver languageDriver;
    protected MapperBuilderAssistant builderAssistant;

    public AbstractMethod(SqlTemplate sqlTemplate) {
        this.sqlTemplate = sqlTemplate;
    }

    public void addMappedStatement(MapperBuilderAssistant builderAssistant,
                                   Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        this.tableInfo = tableInfo;
        this.mapperClass = mapperClass;
        this.configuration = configuration;
        this.builderAssistant = builderAssistant;
        this.languageDriver = configuration.getDefaultScriptingLanguageInstance();

        doAddMappedStatement();
    }

    protected abstract void doAddMappedStatement();

    protected abstract String initSqlScript();

    protected final MappedStatement addSelectMappedStatement(Class<?> resultType) {
        return addMappedStatement(SqlCommandType.SELECT,
                null, resultType, new NoKeyGenerator(), null, null);
    }

    protected MappedStatement addInsertMappedStatement(KeyGenerator keyGenerator) {
        return addMappedStatement(SqlCommandType.INSERT, null,
                Integer.class, keyGenerator, tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
    }

    protected MappedStatement addDeleteMappedStatement() {
        return addMappedStatement(SqlCommandType.DELETE,
                null, Integer.class, new NoKeyGenerator(), null, null);
    }

    protected MappedStatement addUpdateMappedStatement() {
        return addMappedStatement(SqlCommandType.UPDATE, null,
                Integer.class, new NoKeyGenerator(), null, null);
    }

    protected Class<?> getParameterType() {
        return null;
    }

    private MappedStatement addMappedStatement(SqlCommandType sqlCommandType, String resultMap, Class<?> resultType,
                                               KeyGenerator keyGenerator, String keyProperty, String keyColumn) {
        String id = sqlTemplate.getMethod();
        String statementName = mapperClass.getName() + DOT + id;
        if (configuration.hasStatement(statementName)) {
            logger.warn(LEFT_SQUARE_BRACKET + statementName + "] Has been loaded by XML or SqlProvider or Mybatis's Annotation, so ignoring this injection for [" + getClass() + RIGHT_SQUARE_BRACKET);
            return null;
        }
        SqlSource sqlSource = initSqlSource();
        boolean isSelect = sqlCommandType == SqlCommandType.SELECT;
        return builderAssistant.addMappedStatement(id, sqlSource, StatementType.PREPARED, sqlCommandType,
                null, null, null, getParameterType(), resultMap, resultType,
                null, !isSelect, isSelect, false, keyGenerator, keyProperty, keyColumn,
                configuration.getDatabaseId(), languageDriver, null);
    }

    private SqlSource initSqlSource() {
        return languageDriver.createSqlSource(configuration, initSqlScript(), getParameterType());
    }
}
