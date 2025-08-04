package com.archiwork.observability.alerts;

public enum Operator {
    GREATER_THAN,
    LOWER_THAN,
    EQUALS,
    NOT_EQUALS;

    public String toPrometheusOperator() {
        return switch (this) {
            case GREATER_THAN -> ">";
            case LOWER_THAN -> "<";
            case EQUALS -> "==";
            case NOT_EQUALS -> "!=";
        };
    }

    public String toAzureOperator() {
        return switch (this) {
            case GREATER_THAN -> "GreaterThan";
            case LOWER_THAN -> "LessThan";
            case EQUALS -> "Equals";
            case NOT_EQUALS -> "NotEquals";
        };
    }
}