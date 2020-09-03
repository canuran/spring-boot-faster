package ewing.faster.security.vo;

import ewing.query.paging.OffsetPaging;

public class FindRoleParam extends OffsetPaging {

    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
