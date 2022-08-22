package canuran.faster.security.vo;

import canuran.faster.dao.entity.Authority;
import canuran.faster.dao.entity.Role;

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
