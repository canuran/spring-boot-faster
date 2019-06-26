package ewing.faster.dao.entity;

import javax.annotation.Generated;
import java.io.Serializable;

/**
 * Role is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class Role implements Serializable {

    private java.util.Date createTime;

    private String name;

    private java.math.BigInteger roleId;

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.math.BigInteger getRoleId() {
        return roleId;
    }

    public void setRoleId(java.math.BigInteger roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "createTime = " + createTime + ", name = " + name + ", roleId = " + roleId;
    }

}

