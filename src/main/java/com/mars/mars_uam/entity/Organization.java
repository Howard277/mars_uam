package com.mars.mars_uam.entity;

import com.mars.mars_uam.constant.EnableEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 组织机构
 * Created by wuketao on 2018/1/29.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {
        @Index(name = "index_orgName", unique = true, columnList = "orgName"),
        @Index(name = "index_parentOrgCode", unique = false, columnList = "parentOrgCode"),
        @Index(name = "index_address", unique = false, columnList = "address"),
        @Index(name = "index_cityCode", unique = false, columnList = "cityCode"),
        @Index(name = "index_cityName", unique = false, columnList = "cityName"),
        @Index(name = "index_enable", unique = false, columnList = "enable")})
public class Organization implements Serializable{

    /**
     * 机构编码
     */
    @Id
    @Column(length = 100)
    private String orgCode;

    /**
     * 机构名称
     */
    @Column(nullable = false, length = 100)
    private String orgName;

    /**
     * 机构等级
     */
    @Column
    private Integer orgLevel;

    /**
     * 父级机构id
     */
    @Column(length = 100)
    private String parentOrgCode;

    /**
     * 父级机构名称
     */
    @Column(length = 100)
    private String parentOrgName;

    /**
     * 机构地址
     */
    @Column(length = 100)
    private String address;

    /**
     * 城市编码
     */
    @Column(length = 100)
    private String cityCode;

    /**
     * 城市名称
     */
    @Column(length = 100)
    private String cityName;

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

    /**
     * 机构类型, O 机构, F 分中心
     */
    @Column(length = 1,nullable = false)
    private String type;

    /**
     * 来源城市
     */
    @Column(length = 500)
    private String sourceCity;

    /**
     * 是否有效
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EnableEnum enable;
}
