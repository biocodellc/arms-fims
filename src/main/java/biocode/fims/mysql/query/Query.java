package biocode.fims.mysql.query;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * object containing mysql query information. Currently only holds list of {@link SearchCriteria} but can be expanded to
 * ORDER_BYs, etc
 */
public class Query implements Serializable {
    @JsonProperty private List<SearchCriteria> criterion;

    private Query() {}
    public Query(List<SearchCriteria> criterion) {
        this.criterion = criterion;
    }

    public void addCondition(SearchCriteria criteria) {
        this.criterion.add(criteria);
    }

    public List<SearchCriteria> getCriterion() {
        return criterion;
    }
}
