package biocode.fims.mysql.query;

import biocode.fims.digester.DataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Enum containing the valid query operations
 */
public enum Operator {
    EQUALS(DataType.DATETIME, DataType.DATE, DataType.TIME, DataType.FLOAT, DataType.INTEGER, DataType.STRING),
    CONTAINS(DataType.STRING),
    STARTS_WITH(DataType.STRING),
    ENDS_WITH(DataType.STRING),
    IN(DataType.STRING),
    GREATER_THEN(DataType.DATETIME, DataType.DATE, DataType.TIME, DataType.FLOAT, DataType.INTEGER),
    LESS_THEN(DataType.DATETIME, DataType.DATE, DataType.TIME,DataType.FLOAT, DataType.INTEGER);

    private List<DataType> dataTypes = new ArrayList<>();

    Operator(DataType... dataTypes) {
        this.dataTypes = Arrays.asList(dataTypes);
    }

    public List<DataType> getDataTypes() {
        return dataTypes;
    }
}
