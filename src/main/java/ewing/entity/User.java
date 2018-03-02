package ewing.entity;

import javax.annotation.Generated;
import java.io.Serializable;

/**
 * User is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class User implements Serializable {

    private java.sql.Date birthday;

    private java.util.Date createTime;

    private String gender;

    private String nickname;

    private String password;

    private Long userId;

    private String username;

    public java.sql.Date getBirthday() {
        return birthday;
    }

    public void setBirthday(java.sql.Date birthday) {
        this.birthday = birthday;
    }

    public java.util.Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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
        return "birthday = " + birthday + ", createTime = " + createTime + ", gender = " + gender + ", nickname = " + nickname + ", password = " + password + ", userId = " + userId + ", username = " + username;
    }

}

