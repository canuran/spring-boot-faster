package canuran.faster.security.vo;

import canuran.query.paging.OffsetPaging;

public class FindRoleParam extends OffsetPaging {

    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
