package biocode.fims.arms.query;


import biocode.fims.fimsExceptions.FimsRuntimeException;
import biocode.fims.mysql.query.Condition;
import biocode.fims.mysql.query.SearchCriteria;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.List;

/**
 * class to combine multiple {@link SearchCriteria} into a single {@link Predicate} using and
 */
public class DeploymentPredicatesBuilder {
    private final List<SearchCriteria> criterion;

    public DeploymentPredicatesBuilder(List<SearchCriteria> criterion) {
        this.criterion = criterion;
    }

    public Predicate build() {
        if (criterion.size() == 0) {
            return null;
        }

        BooleanBuilder result = null;
        for (final SearchCriteria criteria : criterion) {
            final BooleanExpression exp = DeploymentPredicateFactory.getPredicate(criteria);
            if (exp == null) {
                throw new FimsRuntimeException("Invalid criteria provided. Make sure that the search " +
                        "criteria 'key' is the column_internal value. To get a list of search criteria options visit " +
                        "'data.oceanarms.org/projects/filterOptions'", 400);
            }
            if (result == null) {
                result = new BooleanBuilder(exp);
            } else {
                if (criteria.getCondition().equals(Condition.AND)) {
                    result.and(exp);
                } else if (criteria.getCondition().equals(Condition.OR)) {
                    result.or(exp);
                }
            }
        }

        return result.getValue();
    }
}
