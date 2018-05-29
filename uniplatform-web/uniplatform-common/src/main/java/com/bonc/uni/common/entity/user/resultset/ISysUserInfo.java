package com.bonc.uni.common.entity.user.resultset;

import java.sql.Date;

public interface ISysUserInfo {

    Integer getSysUserId();

    String getSysUserAccount();

    String getSysUserName();

    String getSysUserGender();

    Date getSysUserBirthday();

    String getSysUserHouseholdRegister();

    String getSysUserPosition();

    String getSysUserEmail();

    String getSysUserPhoneNumber();

    String getSysUserCreatorId();

    Integer getSysGroupId();

    String getSysGroupName();

    Integer getSysRoleId();

    String getSysRoleName();

}
