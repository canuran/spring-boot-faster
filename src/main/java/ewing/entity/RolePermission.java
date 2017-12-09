package ewing.entity;

import javax.annotation.Generated;

/**
 * RolePermission is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class RolePermission {

    private java.util.Date createTime;

    private Long permissionId;

    private Long roleId;

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
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

    @Override
    public String toString() {
        return "createTime = " + createTime + ", permissionId = " + permissionId + ", roleId = " + roleId;
    }

}

