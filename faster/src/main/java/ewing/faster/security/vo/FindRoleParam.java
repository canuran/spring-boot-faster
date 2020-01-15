package ewing.faster.security.vo;

import ewing.query.paging.BasePager;

public class FindRoleParam extends BasePager {

    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
