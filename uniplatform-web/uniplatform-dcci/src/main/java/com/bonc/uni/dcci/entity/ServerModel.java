package com.bonc.uni.dcci.entity;

import java.io.Serializable;

/**
 * Created by yedunyao on 2017/9/22.
 */
public class ServerModel implements Serializable {

    int id;

    String ip;

    public ServerModel(int id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
