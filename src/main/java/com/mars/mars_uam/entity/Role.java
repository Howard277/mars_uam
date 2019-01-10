package com.mars.mars_uam.entity;

import com.mars.mars_uam.constant.EnableEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 角色
 * Created by wuketao on 2018/1/30.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {@Index(name = "index_appCode_roleCode", unique = true, columnList = "appCode,roleCode"),
        @Index(name = "inde_roleName", unique = false, columnList = "roleName"),
        @Index(name = "inde_roleCode", unique = false, columnList = "roleCode")
})
public class Role implements Serializable{

    /**
     * 角色编码
     */
    @Id
    @Column(length = 100)
    private String roleCode;

    /**
     * 角色名称
     */
    @Column(nullable = false, length = 100)
    private String roleName;

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
