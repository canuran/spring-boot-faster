package ewing.entity;

import javax.annotation.Generated;

/**
 * Role is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class Role {

    private String code;

    private java.sql.Timestamp createTime;

    private String name;

    private Long roleId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public java.sql.Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.sql.Timestamp createTime) {
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

}

