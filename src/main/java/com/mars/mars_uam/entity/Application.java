package com.mars.mars_uam.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * 应用系统
 * Created by wuketao on 2018/1/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {@Index(name = "index_appName", columnList = "appName", unique = false)})
public class Application {

    /**
     * 应用系统编码
     */
    @Id
    @Column(length = 100)
    private String appCode;

    /**
     * 应用系统名称
     */
    @Column(nullable = false, length = 200)
    private String appName;

    /**
     * 系统根路径
     */
    @Column(length = 200)
    private String url;

    /**
     * 描述
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
