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
    String UPDATE_MAP = "updateMap";
    String PARAM = "param";
    String PARAM_MAP = "paramMap";
    String SELECT_SEGMENT = "selectSegment";
    String WHERE_SEGMENT = "whereSegment";
    String ORDER_BY_SEGMENT = "orderBySegment";
    String GROUP_BY_SEGMENT = "groupBySegment";
    String LAMBDA_DOT = LAMBDA + DOT;
    String LAMBDA_DOT_ENTITY = LAMBDA_DOT + ENTITY;
    String LAMBDA_DOT_ENTITY_DOT = LAMBDA_DOT_ENTITY + DOT;
    String LAMBDA_DOT_PARAM_MAP = LAMBDA_DOT + PARAM_MAP;
    String LAMBDA_DOT_UPDATE_MAP = LAMBDA_DOT + UPDATE_MAP;
    String LAMBDA_DOT_UPDATE_MAP_DOT = LAMBDA_DOT_UPDATE_MAP + DOT;
}
