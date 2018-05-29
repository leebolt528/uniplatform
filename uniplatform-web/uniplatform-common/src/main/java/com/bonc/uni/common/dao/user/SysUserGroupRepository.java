package com.bonc.uni.common.dao.user;

import com.bonc.uni.common.entity.user.SysUserGroup;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface SysUserGroupRepository
        extends CrudRepository<SysUserGroup, Integer>, JpaSpecificationExecutor<SysUserGroup> {

    List<SysUserGroup> findSysUserGroupsByUserIdIn(List <Integer> userIdList);

    void deleteSysUserGroupsByUserId(Integer userId);

    List<SysUserGroup> findSysUserGroupsByGroupIdIn(List <Integer> groupIdList);

}
