package com.mars.mars_uam.entity;

import com.mars.mars_uam.constant.EnableEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 * Created by wuketao on 2018/1/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {@Index(name="index_userName",unique = false,columnList = "userName"),
    @Index(name = "index_orgCode",unique = false,columnList = "orgCode")})
public class User implements Serializable{

    /**
     * 登录名
     */
    @Id
    @Column(length = 100)
    private String loginName;

    /**
     * 密码
     */
    @Column(nullable = false, length = 64)
    private String password;

    /**
     * 真实姓名
     */
    @Column(nullable = false, length = 20)
    private String userName;

    /**
     * 机构id
     */
    @Column(nullable = false, length = 100)
    private String orgCode;

    /**
     * 机构名称
     */
    @Column(nullable = false, length = 100)
    private String orgName;

    /**
     * 角色配置
     */
    @Column(length = 1000)
    private String roles;

    /**
     * 是否有效
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnableEnum enable;

    /**
     * 创建人
     */
    @Column(nullable = false, length = 50)
    private String creator;

    /**
     * 创建时间
     */
    @Column(nullable = false)
    private Date createTime;

    /**
     * 更新人
     */
    @Column(length = 50)
    private String updateUser;

    /**
     * 更新时间
     */
    @Column
    private Date updateTime;
}
