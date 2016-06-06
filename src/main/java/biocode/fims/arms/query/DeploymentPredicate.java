package biocode.fims.arms.query;

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
class DeploymentPredicate {
    BooleanExpression getPredicate(SearchCriteria criteria) {
        try {
            Field field = QDeployment.class.getDeclaredField(criteria.getKey());
            StringExpression path = null;
            Path<?> val = (Path<?>) field.get(new QDeployment("deployment"));
            if (val instanceof NumberPath) {
                path = ((NumberPath) val).stringValue();
            } else if (val instanceof StringPath) {
                path = (StringExpression) val;
            } else if (val instanceof DateTimePath) {
                path = ((DateTimePath) val).stringValue();
            } else if (val instanceof ComparablePath) {
                throw new FimsRuntimeException("Invalid Query Key. Cannot query sql relations", 400);
            }
            if (path != null) {
                if (criteria.getOperator().equals(Operator.EQUALS)) {
                    return path.eq(criteria.getValue());
                } else if (criteria.getOperator().equals(Operator.CONTAINS)) {
                    return path.contains(criteria.getValue());
                } else if (criteria.getOperator().equals(Operator.ENDS_WITH)) {
                    return path.endsWith(criteria.getValue());
                } else if (criteria.getOperator().equals(Operator.STARTS_WITH)) {
                    return path.startsWith(criteria.getValue());
                }
            }
        } catch (IllegalAccessException|NoSuchFieldException ignored) {}
        return null;
    }
}
