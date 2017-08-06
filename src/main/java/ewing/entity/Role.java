package ewing.entity;

import javax.annotation.Generated;

/**
 * Role is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class Role {

    private String code;

    private String name;

    private Integer roleId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

}

