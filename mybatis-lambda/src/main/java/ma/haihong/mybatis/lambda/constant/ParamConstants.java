package ma.haihong.mybatis.lambda.constant;

import static ma.haihong.mybatis.lambda.constant.CommonConstants.DOT;

/**
 * @author haihong.ma
 */
public interface ParamConstants {
    String LAMBDA = "ml";
    String COLLECTION = "coll";
    String LIST = "list";
    String ITEM = "item";
    String ENTITY = "entity";
    String SQL_SEGMENT = "sqlSegment";
    String LAMBDA_ENTITY_DOT = LAMBDA + DOT + ENTITY + DOT;
    String LAMBDA_SQL_SEGMENT = LAMBDA + DOT + SQL_SEGMENT;
}
