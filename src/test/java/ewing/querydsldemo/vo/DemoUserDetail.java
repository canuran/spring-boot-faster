package ewing.querydsldemo.vo;

import ewing.querydsldemo.entity.DemoUser;

/**
 * 用户详细信息。
 */
public class DemoUserDetail extends DemoUser {

    private String genderName;

    private String addressName;

    public String getGenderName() {
        return genderName;
    }

    public void setGenderName(String genderName) {
        this.genderName = genderName;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }
}

