package com.bonc.uni.common.entity.user;

import com.bonc.uni.common.util.user.SysUserStatus;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "sys_user")
public class SysUser implements Serializable {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户名
     */
    @Column(name = "account")
    private String account;

    /**
     * 密码
     */
    @Column(name = "passwd")
    private String passwd;

    /**
     * 用户姓名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 性别
     */
    @Column(name = "gender")
    private String gender;

    /**
     * 生日
     */
    @Column(name = "birthday")
    private String birthday;

    /**
     * 户籍
     */
    @Column(name = "household_register")
    private String householdRegister;

    /**
     * 职位
     */
    @Column(name = "position")
    private String position;

    /**
     * 邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 手机号
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 创建者id
     */
    @Column(name = "creator_id")
    private Integer creatorId;

    /**
     * 附加信息
     */
    @Column(name = "additional_info")
    private String additionalInfo;

    /**
     * 保留字段
     */
    @Column(name = "reservedfileld1")
    private String reservedfileld1;

    @Column(name = "reservedfileld2")
    private String reservedfileld2;

    @Column(name = "reservedfileld3")
    private String reservedfileld3;

    /**
     * 状态
     */
    @Column(name = "status")
    @Enumerated
    private SysUserStatus status = SysUserStatus.OPEN;

}

