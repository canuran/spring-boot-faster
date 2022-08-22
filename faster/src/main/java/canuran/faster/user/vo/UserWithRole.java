package canuran.faster.user.vo;

import canuran.faster.dao.entity.Role;
import canuran.faster.dao.entity.User;

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
