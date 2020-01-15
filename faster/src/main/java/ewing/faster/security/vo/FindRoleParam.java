package ewing.faster.security.vo;

import ewing.query.paging.OffsetPager;

public class FindRoleParam extends OffsetPager {

    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
