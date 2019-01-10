package com.mars.mars_uam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户-角色关系
 * Created by wuketao on 2018/1/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {@Index(name = "index_appCode_roleCode_loginName",unique = true,columnList = "appCode,roleCode,loginName")})
public class UserRole {
    /**
     * 用户-角色关系id 自增主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 应用系统编码
     */
    @Column(nullable = false, length = 100)
    private String appCode;

    /**
     * 角色编码
     */
    @Column(nullable = false, length = 100)
    private String roleCode;

    /**
     * 登录名
     */
    @Column(nullable = false, length = 100)
    private String loginName;

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
}
