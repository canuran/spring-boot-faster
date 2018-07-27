package ewing.common.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigInteger;
import java.util.List;

public class DictionaryNode {

    private BigInteger dictionaryId;

    private BigInteger parentId;

    private BigInteger rootId;

    private String name;

    private String value;

    private List<DictionaryNode> children;

    public BigInteger getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(BigInteger dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    public BigInteger getParentId() {
        return parentId;
    }

    public void setParentId(BigInteger parentId) {
        this.parentId = parentId;
    }

    @JsonIgnore
    public BigInteger getRootId() {
        return rootId;
    }

    public void setRootId(BigInteger rootId) {
        this.rootId = rootId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<DictionaryNode> getChildren() {
        return children;
    }

    public void setChildren(List<DictionaryNode> children) {
        this.children = children;
    }
}
