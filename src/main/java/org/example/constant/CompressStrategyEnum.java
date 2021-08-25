package org.example.constant;

public enum  CompressStrategyEnum {

    GZIP("");

    CompressStrategyEnum(String type) {
        this.type = type;
    }

    private String type;
}
