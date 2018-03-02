package ewing.common.vo;

import ewing.application.query.Pager;

public class FindDictionaryParam extends Pager {

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
