package ewing.querydsldemo.vo;

/**
 * 城市用户统计。
 */
public class DemoAddressUser {

    private String city;

    private Long totalUser;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getTotalUser() {
        return totalUser;
    }

    public void setTotalUser(Long totalUser) {
        this.totalUser = totalUser;
    }
}
