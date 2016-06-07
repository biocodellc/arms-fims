package biocode.fims.mysql.query;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object to hold the querydsl search criteria
 */
public class SearchCriteria {
    @JsonProperty private String key;
    @JsonProperty private Operator operator;
    // if operator is IN, the value is a , separated string of values
    @JsonProperty private String value;
    @JsonProperty private Condition condition;

    private SearchCriteria() {}

    public SearchCriteria(String key, Operator operator, String value) {
        this.key = key;
        this.operator = operator;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Operator getOperator() {
        return operator;
    }

    public String getValue() {
        return value;
    }

    public Condition getCondition() {
        return condition;
    }
}
