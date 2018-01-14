package cn.chestnut.mvvm.teamworker.module.massage.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by king on 2018/1/14.
 */
@Entity
public class MessageUser {

    @Id(autoincrement = true)
    private Long id;

    private String userId;

    private String nickname;

    @Generated(hash = 1622123696)
    public MessageUser(Long id, String userId, String nickname) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
    }

    @Generated(hash = 1201440511)
    public MessageUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
