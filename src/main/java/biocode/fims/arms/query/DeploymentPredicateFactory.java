package biocode.fims.arms.query;

import biocode.fims.arms.entities.QArmsExpedition;
import biocode.fims.arms.entities.QDeployment;
import biocode.fims.fimsExceptions.FimsRuntimeException;
import biocode.fims.mysql.query.Operator;
import biocode.fims.mysql.query.SearchCriteria;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;

import java.lang.reflect.Field;

/**
 * Class to turn a {@link SearchCriteria} into a {@link com.querydsl.core.types.Predicate}
 */
class DeploymentPredicateFactory {
    static BooleanExpression getPredicate(SearchCriteria criteria) {
        try {
            Field field = QDeployment.class.getDeclaredField(criteria.getKey());
            String criteriaValue = criteria.getValue();
            Path<?> val = (Path<?>) field.get(new QDeployment("deployment"));

            // for now, if we see armsExpedition, then just assume we are querying the expeditionId. It would be nice
            // to make this a generic method and pass in the QClass as a param, however I'm not sure how to do nested
            // paths in the criteria.getKey() ex "armsExpedition.expeditionId". We would need to do some type of parsing
            if (val instanceof QArmsExpedition) {
                return ((QArmsExpedition) val).expeditionId.eq(Integer.valueOf(criteriaValue));
            }

            if (val != null) {
                if (criteria.getOperator().equals(Operator.EQUALS)) {
                    if (val instanceof NumberPath) {
                        return ((NumberPath) val).eq(Float.parseFloat(criteriaValue));
                    } else if (val instanceof StringPath) {
                        return ((StringPath) val).eq(criteriaValue);
                    } else {
                        throw new FimsRuntimeException("Invalid Query Operator for column.", 400);
                    }
                } else if (criteria.getOperator().equals(Operator.CONTAINS)) {
                    if (val instanceof StringPath) {
                        return ((StringPath) val).contains(criteriaValue);
                    } else {
                        throw new FimsRuntimeException("Invalid Query Operator for column.", 400);
                    }
                } else if (criteria.getOperator().equals(Operator.ENDS_WITH)) {
                    if (val instanceof StringPath) {
                        return ((StringPath) val).endsWith(criteriaValue);
                    } else {
                        throw new FimsRuntimeException("Invalid Query Operator for column.", 400);
                    }
                } else if (criteria.getOperator().equals(Operator.STARTS_WITH)) {
                    if (val instanceof StringPath) {
                        return ((StringPath) val).startsWith(criteriaValue);
                    } else {
                        throw new FimsRuntimeException("Invalid Query Operator for column.", 400);
                    }
                } else if (criteria.getOperator().equals(Operator.GREATER_THEN)) {
                    if (val instanceof NumberPath) {
                        return ((NumberPath) val).gt(Float.parseFloat(criteriaValue));
                    } else if (val instanceof StringPath) {
                        return ((StringPath) val).gt(criteriaValue);
                    }
                } else if (criteria.getOperator().equals(Operator.LESS_THEN)) {
                    if (val instanceof NumberPath) {
                        return ((NumberPath) val).lt(Float.parseFloat(criteriaValue));
                    } else if (val instanceof StringPath) {
                        return ((StringPath) val).lt(criteriaValue);
                    }
                } else if (criteria.getOperator().equals(Operator.IN)) {
                    // split string removing any whitespace
                    if (val instanceof StringPath) {
                        return ((StringPath) val).in(criteriaValue.trim().split("\\s,\\s|,\\s|,"));
                    } else {
                        throw new FimsRuntimeException("Invalid Query Operator for column.", 400);
                    }
                }
            }
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
        }
        return null;
    }
}
