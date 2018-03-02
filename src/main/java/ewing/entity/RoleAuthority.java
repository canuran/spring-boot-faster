package ewing.entity;

import javax.annotation.Generated;
import java.io.Serializable;

/**
 * RoleAuthority is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class RoleAuthority implements Serializable {

    private Long authorityId;

    private java.util.Date createTime;

    private Long roleId;

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

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

    @Override
    public String toString() {
        return "authorityId = " + authorityId + ", createTime = " + createTime + ", roleId = " + roleId;
    }

}

