package com.mars.mars_uam.entity;

import com.mars.mars_uam.constant.EnableEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 资源
 * Created by wuketao on 2018/1/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
        @Index(name = "index_appCode_resourceCode", unique = true, columnList = "appCode,resourceCode"),
        @Index(name = "index_resourceCode", unique = false, columnList = "resourceCode"),
        @Index(name = "index_resourceName", unique = false, columnList = "resourceName"),
        @Index(name = "index_resourcePath", unique = false, columnList = "resourcePath")})
public class Resource implements Serializable{
    /**
     * 资源编码
     */
    @Id
    @Column(length = 100)
    private String resourceCode;

    /**
     * 资源名称
     */
    @Column(length = 100)
    private String resourceName;

    /**
     * 父级资源编码
     */
    @Column(length = 100)
    private String parentResourceCode;

    /**
     * 父级资源名称
     */
    @Column(length = 100)
    private String parentResourceName;

    /**
     * 资源路径
     */
    @Column(length = 200)
    private String resourcePath;

    /**
     * 资源排序编号
     */
    @Column
    private Integer sortNo;

    /**
     * 应用系统编码
     */
    @Column(length = 100)
    private String appCode;

    /**
     * 应用系统名称
     */
    @Column(length = 100)
    private String appName;

    /**
     * 是否有效
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnableEnum enable;

    /**
     * 角色配置
     */
    @Column(length = 1000)
    private String roles;

    /**
     * 备注
     */
    @Column(length = 200)
    private String description;

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
