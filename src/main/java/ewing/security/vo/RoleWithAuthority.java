package ewing.security.vo;

import ewing.entity.Authority;
import ewing.entity.Role;

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
