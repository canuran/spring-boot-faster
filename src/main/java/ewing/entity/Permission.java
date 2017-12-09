package ewing.entity;

import javax.annotation.Generated;

/**
 * Permission is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class Permission {

    private String code;

    private java.util.Date createTime;

    private String name;

    private Long parentId;

    private Long permissionId;

    private String target;

    private String type;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "code = " + code + ", createTime = " + createTime + ", name = " + name + ", parentId = " + parentId + ", permissionId = " + permissionId + ", target = " + target + ", type = " + type;
    }

}

