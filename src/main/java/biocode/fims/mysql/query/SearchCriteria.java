package biocode.fims.mysql.query;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Object to hold the querydsl search criteria
 */
public class SearchCriteria {
    @JsonProperty private String key;
    @JsonProperty private Operator operator;
    @JsonProperty private String value;

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
}
