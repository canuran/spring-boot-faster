package ewing.faster.security.vo;

import ewing.faster.dao.entity.Authority;
import ewing.faster.dao.entity.Role;

import java.util.List;

public class RoleWithAuthority extends Role {

    List<Authority> authorities;

    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }
}
