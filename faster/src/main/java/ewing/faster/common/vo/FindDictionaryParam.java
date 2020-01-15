package ewing.faster.common.vo;

import ewing.query.paging.BasePager;

public class FindDictionaryParam extends BasePager {

    private String name;

    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
