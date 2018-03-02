package ewing.entity;

import javax.annotation.Generated;
import java.io.Serializable;

/**
 * UserRole is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class UserRole implements Serializable {

    private java.util.Date createTime;

    private Long roleId;

    private Long userId;

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
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

