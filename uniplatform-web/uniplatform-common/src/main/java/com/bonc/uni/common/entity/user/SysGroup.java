package com.bonc.uni.common.entity.user;

import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "sys_group")
public class SysGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户组名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 用户组代码
     */
    @Column(name = "code")
    private String code;

    /**
     * 权限
     */
    @Column(name = "permission")
    private String permission;

    /**
     * 创建者id
     */
    @Column(name = "creator_id")
    private Integer creatorId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "additional_info")
    private String additionalInfo;

    @Column(name = "reservedfileld1")
    private String reservedfileld1;

    @Column(name = "reservedfileld2")
    private String reservedfileld2;

    @Column(name = "reservedfileld3")
    private String reservedfileld3;

    public SysGroup(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
