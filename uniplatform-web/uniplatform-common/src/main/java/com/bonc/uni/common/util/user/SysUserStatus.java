package com.bonc.uni.common.util.user;

import java.util.HashMap;
import java.util.Map;


public enum SysUserStatus {

    OPEN("正常"),
    LOCKED("锁定"),
    EXPIRED("密码过期"),
    UNDEFINED("未定义状态");

    private String desc;

    private static Map<String, SysUserStatus> sysUserStatusMap = new HashMap<>();

    static {
        for (SysUserStatus sysUserStatus : values()) {
            sysUserStatusMap.put(sysUserStatus.toString(), sysUserStatus);
        }
    }

    SysUserStatus(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 将字符串转为 SysUserStatus
     * @param userStatusStr 字符串
     * @return 对应枚举类型或则 SysUserStatus.UNDEFINED
     */
    public static SysUserStatus convertToUserStatus(String userStatusStr) {
        SysUserStatus sysUserStatus = sysUserStatusMap.get(userStatusStr);
        if (sysUserStatus == null) {
            sysUserStatus = SysUserStatus.UNDEFINED;
        }
        return sysUserStatus;
    }

}
