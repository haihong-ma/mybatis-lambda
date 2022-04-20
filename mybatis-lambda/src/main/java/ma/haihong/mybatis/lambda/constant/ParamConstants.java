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
    String PARAM = "param";
    String PARAM_MAP = "paramMap";
    String TABLE_PREFIX = "tablePrefix";
    String SELECT_SEGMENT = "selectSegment";
    String JOIN_SEGMENT = "joinSegment";
    String WHERE_SEGMENT = "whereSegment";
    String ORDER_BY_SEGMENT = "orderBySegment";
    String GROUP_BY_SEGMENT = "groupBySegment";
    String LAMBDA_ENTITY_DOT = LAMBDA + DOT + ENTITY + DOT;
    String LAMBDA_DOT = LAMBDA + DOT;
    String LAMBDA_DOT_PARAM_MAP = LAMBDA + DOT + PARAM_MAP;
}
