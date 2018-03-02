package ewing.entity;

import javax.annotation.Generated;
import java.io.Serializable;

/**
 * Permission is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class Permission implements Serializable {

    private String action;

    private java.util.Date createTime;

    private Long permissionId;

    private String targetId;

    private String targetType;

    private Long userId;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

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

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "action = " + action + ", createTime = " + createTime + ", permissionId = " + permissionId + ", targetId = " + targetId + ", targetType = " + targetType + ", userId = " + userId;
    }

}

