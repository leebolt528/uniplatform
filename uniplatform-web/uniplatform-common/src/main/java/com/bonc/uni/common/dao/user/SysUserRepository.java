package com.bonc.uni.common.dao.user;


import com.bonc.uni.common.entity.user.SysUser;
import com.bonc.uni.common.entity.user.resultset.ISysUserGroupBySysGroup;
import com.bonc.uni.common.entity.user.resultset.ISysUserInfo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SysUserRepository extends CrudRepository<SysUser, Integer>, JpaSpecificationExecutor<SysUser> {

	SysUser getSysUserByAccountIs(String account);

	@Query(value = "SELECT t FROM SysUser t WHERE t.account LIKE ?1")
	List<SysUser> findSysUserLikeAccount(String account);

    @Query(
            "SELECT " +
            "    su.id as sysUserId, " +
            "    su.account as sysUserAccount, " +
            "    su.phoneNumber as sysUserPhoneNumber, " +
            "    su.email as sysUserEmail, " +
            "    su.creatorId as sysUserCreatorId, " +
            "    sg.id as sysGroupId, " +
            "    sg.name as sysGroupName, " +
            "    sr.id as sysRoleId, " +
            "    sr.name as sysRoleName " +
            "FROM " +
            "    SysUser su, " +
            "    SysUserGroup sug, " +
            "    SysGroup sg, " +
            "    SysUserRole sur, " +
            "    SysRole sr " +
            "WHERE " +
            "    su.id = sug.userId " +
            "AND su.id = sur.userId " +
            "AND sr.id = sur.roleId " +
            "AND sg.id = sug.groupId "
    )
    List<ISysUserInfo> getAllSysUserInfo();

	@Query(
	        "SELECT " +
            "    su.id as sysUserId, " +
            "    su.account as sysUserAccount, " +
            "    su.phoneNumber as sysUserPhoneNumber, " +
            "    su.email as sysUserEmail, " +
            "    su.creatorId as sysUserCreatorId, " +
            "    sg.id as sysGroupId, " +
            "    sg.name as sysGroupName, " +
            "    sr.id as sysRoleId, " +
            "    sr.name as sysRoleName " +
            "FROM " +
            "    SysUser su, " +
            "    SysUserGroup sug, " +
            "    SysGroup sg, " +
            "    SysUserRole sur, " +
            "    SysRole sr " +
            "WHERE " +
            "    sg.id = ?1 " +
            "AND su.id = sug.userId " +
            "AND su.id = sur.userId " +
            "AND sr.id = sur.roleId " +
            "AND sg.id = sug.groupId "
    )
	List<ISysUserInfo> getSysUserInfoWithSysGroupId(Integer sysGroupId);

	@Query(
	        "SELECT " +
            "    sg.id AS sysGroupId, " +
            "    sg.name AS sysGroupName, " +
            "    su.id AS sysUserId, " +
            "    su.userName AS sysUserName " +
            "FROM " +
            "    SysUser su, " +
            "    SysGroup sg, " +
            "    SysUserGroup sug " +
            "WHERE " +
            "    sug.groupId = sg.id " +
            "AND sug.userId = su.id " +
            "ORDER BY sysGroupId, sysUserId "
	)
	List<ISysUserGroupBySysGroup> getSysUserInfoGroupBySysGroup();

	@Query(
            "SELECT " +
            "    su.account as sysUserAccount, " +
            "    su.userName as sysUserName, " +
            "    su.gender as sysUserGender, " +
            "    su.birthday as sysUserBirthday, " +
            "    su.householdRegister as sysUserHouseholdRegister, " +
            "    su.position as sysUserPosition, " +
            "    su.phoneNumber as sysUserPhoneNumber, " +
            "    su.email as sysUserEmail, " +
            "    sg.name as sysGroupName, " +
            "    sr.name as sysRoleName " +
            "FROM " +
            "    SysUser su, " +
            "    SysUserGroup sug, " +
            "    SysGroup sg, " +
            "    SysUserRole sur, " +
            "    SysRole sr " +
            "WHERE " +
            "    su.id = sug.userId " +
            "AND su.id = ?1 " +
            "AND su.id = sur.userId " +
            "AND sr.id = sur.roleId " +
            "AND sg.id = sug.groupId "
    )
	ISysUserInfo getSysUserInfoById(Integer sysUserId);

}
