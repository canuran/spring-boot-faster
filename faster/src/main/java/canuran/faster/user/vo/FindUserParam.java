package canuran.faster.user.vo;

import canuran.query.paging.OffsetPaging;

public class FindUserParam extends OffsetPaging {

    private String username;

    private String nickname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
