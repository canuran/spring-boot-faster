package ewing.entity;

import javax.annotation.Generated;

/**
 * Role is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class Role {

    private String code;

    private java.util.Date createTime;

    private String name;

    private Long roleId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "code = " + code + ", createTime = " + createTime + ", name = " + name + ", roleId = " + roleId;
    }

}

