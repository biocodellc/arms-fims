package biocode.fims.arms.query;


import biocode.fims.mysql.query.SearchCriteria;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.ArrayList;
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

        final List<BooleanExpression> predicates = new ArrayList<>();
        DeploymentPredicate predicate = new DeploymentPredicate();
        for (final SearchCriteria param : criterion) {
            final BooleanExpression exp = predicate.getPredicate(param);
            if (exp != null) {
                predicates.add(exp);
            }
        }

        BooleanExpression result = predicates.get(0);
        for (int i = 1; i < predicates.size(); i++) {
            result = result.and(predicates.get(i));
        }
        return result;
    }
}
