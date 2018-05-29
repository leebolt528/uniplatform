package com.bonc.uni.common.dao.user;

import com.bonc.uni.common.entity.user.SysRole;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SysRoleRepository extends CrudRepository<SysRole, Integer>, JpaSpecificationExecutor<SysRole> {

    SysRole findSysRoleByName(String roleName);

    @Query("SELECT new SysRole(s.id, s.name) FROM SysRole s")
    List<SysRole> listIdAndName();

    void deleteSysRolesByIdIn(List <Integer> roleIdList);

}
