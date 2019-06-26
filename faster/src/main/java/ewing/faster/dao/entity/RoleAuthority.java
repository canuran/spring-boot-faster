package ewing.faster.dao.entity;

import javax.annotation.Generated;
import java.io.Serializable;

/**
 * RoleAuthority is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class RoleAuthority implements Serializable {

    private java.math.BigInteger authorityId;

    private java.util.Date createTime;

    private java.math.BigInteger roleId;

    public java.math.BigInteger getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(java.math.BigInteger authorityId) {
        this.authorityId = authorityId;
    }

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

    @Override
    public String toString() {
        return "authorityId = " + authorityId + ", createTime = " + createTime + ", roleId = " + roleId;
    }

}

