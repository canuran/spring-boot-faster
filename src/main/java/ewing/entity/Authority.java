package ewing.entity;

import javax.annotation.Generated;
import java.io.Serializable;

/**
 * Authority is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class Authority implements Serializable {

    private Long authorityId;

    private String code;

    private String content;

    private java.util.Date createTime;

    private String name;

    private Long parentId;

    private String type;

    public Long getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(Long authorityId) {
        this.authorityId = authorityId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "authorityId = " + authorityId + ", code = " + code + ", content = " + content + ", createTime = " + createTime + ", name = " + name + ", parentId = " + parentId + ", type = " + type;
    }

}

