package ma.haihong.mybatis.lambda.parsing;

import java.util.Map;

/**
 * @author haihong.ma
 */
public class ParsedResult {

   private final String whereSegment;

   private final Map<String,Object> paramMap;

    public ParsedResult(String whereSegment, Map<String, Object> paramMap) {
        this.whereSegment = whereSegment;
        this.paramMap = paramMap;
    }

    public String getWhereSegment() {
        return whereSegment;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }
}
