package ewing.entity;

import javax.annotation.Generated;

/**
 * UserPermission is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class UserPermission {

    private java.util.Date createTime;

    private Long permissionId;

    private Long userId;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "createTime = " + createTime + ", permissionId = " + permissionId + ", userId = " + userId;
    }

}

