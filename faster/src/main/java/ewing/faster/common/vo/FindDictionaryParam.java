package ewing.faster.common.vo;

import ewing.query.paging.OffsetPager;

public class FindDictionaryParam extends OffsetPager {

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
