package ewing.user.vo;

import ewing.entity.Role;
import ewing.entity.User;

import java.util.List;

public class UserWithRole extends User {

    private List<Role> roles;

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
