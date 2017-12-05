package ewing.entity;

import javax.annotation.Generated;

/**
 * UserRole is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class UserRole {

    private java.sql.Timestamp createTime;

    private Long roleId;

    private Long userId;

    public java.sql.Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.sql.Timestamp createTime) {
        this.createTime = createTime;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "createTime = " + createTime + ", roleId = " + roleId + ", userId = " + userId;
    }

}

