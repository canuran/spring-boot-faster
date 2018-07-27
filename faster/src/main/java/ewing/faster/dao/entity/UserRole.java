package ewing.faster.dao.entity;

import javax.annotation.Generated;
import java.io.Serializable;

/**
 * UserRole is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class UserRole implements Serializable {

    private java.util.Date createTime;

    private java.math.BigInteger roleId;

    private java.math.BigInteger userId;

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public java.math.BigInteger getRoleId() {
        return roleId;
    }

    public void setRoleId(java.math.BigInteger roleId) {
        this.roleId = roleId;
    }

    public java.math.BigInteger getUserId() {
        return userId;
    }

    public void setUserId(java.math.BigInteger userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
         return "createTime = " + createTime + ", roleId = " + roleId + ", userId = " + userId;
    }

}

