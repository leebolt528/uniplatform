package com.bonc.uni.common.dao.user;

import com.bonc.uni.common.entity.user.SysUserRole;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SysUserRoleRepository
        extends CrudRepository<SysUserRole, Integer>, JpaSpecificationExecutor<SysUserRole> {

    List<SysUserRole> findSysUserRolesByUserIdIn(List <Integer> userIdList);

    void deleteSysUserRolesByUserId(Integer userId);

    List<SysUserRole> findSysUserRolesByRoleIdIn(List <Integer> roleIdList);

}
