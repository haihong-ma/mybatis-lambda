package ma.haihong.mybatis.lambda.parsing.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author haihong.ma
 */
public class ParsedCache {

    /**
     * 是否包含变量
     */
    private final boolean hasCapturedArg;

    /**
     * 参数名及对应参数Map
     */
    private final HashMap<String, Object> paramMap;
    /**
     * 因为存在IN操作符时，需要通过参数列表长度构造对应参数名，所以拆分成多条语句
     */
    private final Map<String, String> paramSqlSegmentsMap;

    public ParsedCache(HashMap<String, Object> paramMap, Map<String, String> paramSqlSegmentsMap, boolean hasCapturedArg) {
        this.paramMap = paramMap;
        this.hasCapturedArg = hasCapturedArg;
        this.paramSqlSegmentsMap = paramSqlSegmentsMap;
    }

    /**
     * 因需要添加额外参数，故返回参数副本
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCloneParamMap() {
        return (Map<String, Object>) paramMap.clone();
    }

    public Map<String, String> getParamSqlSegmentsMap() {
        return paramSqlSegmentsMap;
    }

    public boolean hasCapturedArg() {
        return hasCapturedArg;
    }
}
