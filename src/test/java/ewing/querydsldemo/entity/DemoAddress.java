package ewing.querydsldemo.entity;

import javax.annotation.Generated;

/**
 * DemoAddress is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class DemoAddress {

    private Integer addressId;

    private String city;

    private String county;

    private String province;

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

}

