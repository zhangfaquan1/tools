package org.example.constant;

public enum  CompressStrategyEnum {

    GZIP("org.example.util.compress.GzipStrategy");

    CompressStrategyEnum(String type) {
        this.strategyName = type;
    }

    private String strategyName;

    public String getStrategyName() {
        return strategyName;
    }
}
