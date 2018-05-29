package com.bonc.uni.common.dao.user;

import com.bonc.uni.common.entity.user.SysGroup;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SysGroupRepository extends CrudRepository<SysGroup, Integer>,
        JpaSpecificationExecutor<SysGroup> {

    SysGroup findSysGroupByName(String groupName);

    @Query("SELECT new SysGroup(s.id, s.name) FROM SysGroup s")
    List<SysGroup> listIdAndName();

    void deleteSysGroupsByIdIn(List <Integer> groupIdList);

}