package com.bonc.uni.usou.entity;

import java.io.Serializable;

/**
 * Created by yedunyao on 2018/1/25.
 */
public class ClusterInfo implements Serializable {

    private static String AUTHSPLIT = ":";

    int id;

    String[] usrls;

    /*String authInfo;*/

    String user = "";

    String password = "";

    public ClusterInfo() {
    }

    public ClusterInfo(String[] usrls, String user, String password) {
        this.usrls = usrls;
        this.user = user;
        this.password = password;
    }

    public ClusterInfo(int id, String[] usrls, String user, String password) {
        this.id = id;
        this.usrls = usrls;
        this.user = user;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getUsrls() {
        return usrls;
    }

    public void setUsrls(String[] usrls) {
        this.usrls = usrls;
    }

    public String getAuthInfo() {
        return user + AUTHSPLIT + password;
    }

    /*public void setAuthInfo(String authInfo) {
        this.authInfo = authInfo;
    }*/

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user == null ? "" : user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? "" : password;
    }
}
