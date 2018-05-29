package com.bonc.uni.common.entity.user;

import lombok.*;

import javax.persistence.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "sys_user_group")
public class SysUserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "group_id")
    private Integer groupId;

}