package ewing.entity;

import javax.annotation.Generated;

/**
 * User is a Querydsl bean type
 */
@Generated("com.querydsl.codegen.BeanSerializer")
public class User {

    private java.sql.Timestamp birthday;

    private java.sql.Timestamp createTime;

    private String password;

    private Long userId;

    private String username;

    public java.sql.Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(java.sql.Timestamp birthday) {
        this.birthday = birthday;
    }

    public java.sql.Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.sql.Timestamp createTime) {
        this.createTime = createTime;
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

}

