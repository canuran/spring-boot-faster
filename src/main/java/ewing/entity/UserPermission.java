package ewing.entity;

import javax.annotation.Generated;

/**
 * UserPermission is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class UserPermission {

    private java.sql.Timestamp createTime;

    private Long permissionId;

    private Long userId;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}

