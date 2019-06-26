package ewing.faster.dao.entity;

import javax.annotation.Generated;
import java.io.Serializable;

/**
 * Dictionary is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class Dictionary implements Serializable {

    private java.util.Date createTime;

    private String detail;

    private java.math.BigInteger dictionaryId;

    private String name;

    private java.math.BigInteger parentId;

    private java.math.BigInteger rootId;

    private String value;

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public java.math.BigInteger getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(java.math.BigInteger dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.math.BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(java.math.BigInteger parentId) {
        this.parentId = parentId;
    }

    public java.math.BigInteger getRootId() {
        return rootId;
    }

    public void setRootId(java.math.BigInteger rootId) {
        this.rootId = rootId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "createTime = " + createTime + ", detail = " + detail + ", dictionaryId = " + dictionaryId + ", name = " + name + ", parentId = " + parentId + ", rootId = " + rootId + ", value = " + value;
    }

}

