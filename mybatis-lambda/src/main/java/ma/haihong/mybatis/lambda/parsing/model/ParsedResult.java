package ma.haihong.mybatis.lambda.parsing.model;

import java.util.Map;

/**
 * @author haihong.ma
 */
public class ParsedResult {
    private final String sqlSegment;
    private final Map<String, Object> paramMap;

    public ParsedResult(String sqlSegment, Map<String, Object> paramMap) {
        this.sqlSegment = sqlSegment;
        this.paramMap = paramMap;
    }

    public String getSqlSegment() {
        return sqlSegment;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }
}
