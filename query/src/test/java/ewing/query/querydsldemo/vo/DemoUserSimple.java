package ewing.query.querydsldemo.vo;

/**
 * 用户简略信息。
 */
public class DemoUserSimple {

    private Integer gender;

    private Integer userId;

    private String username;

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "DemoUserSimple{" +
                "gender=" + gender +
                ", userId=" + userId +
                ", username='" + username + '\'' +
                '}';
    }
}

