package ewing.faster.security.vo;

import ewing.query.paging.Pager;

public class FindRoleParam extends Pager {

    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
