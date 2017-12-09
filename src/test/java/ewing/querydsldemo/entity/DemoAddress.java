package ewing.querydsldemo.entity;

import javax.annotation.Generated;

/**
 * DemoAddress is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class DemoAddress {

    private Integer addressId;

    private String name;

    private Integer parentId;

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
         return "addressId = " + addressId + ", name = " + name + ", parentId = " + parentId;
    }

}

