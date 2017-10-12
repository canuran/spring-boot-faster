package ewing.entity;

import javax.annotation.Generated;

/**
 * RolePermission is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class RolePermission {

    private java.sql.Timestamp createTime;

    private Long permissionId;

    private Long roleId;

    public java.sql.Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.sql.Timestamp createTime) {
        this.createTime = createTime;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

}

