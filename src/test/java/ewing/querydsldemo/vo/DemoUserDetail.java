package ewing.querydsldemo.vo;

import ewing.querydsldemo.entity.DemoUser;

/**
 * 用户详细信息。
 */
public class DemoUserDetail extends DemoUser {

    private String genderName;

    private String addressCity;

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public void setAddressCity(String addressCity) {
        this.addressCity = addressCity;
    }
}

