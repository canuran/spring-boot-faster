package ewing.entity;

import javax.annotation.Generated;

/**
 * Authority is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class Authority {

    private Long authorityId;

    private String code;

    private java.util.Date createTime;

    private String name;

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

    @Override
    public String toString() {
        return "authorityId = " + authorityId + ", code = " + code + ", createTime = " + createTime + ", name = " + name;
    }

}

