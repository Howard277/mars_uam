package com.mars.mars_uam.repository;

import com.mars.mars_uam.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by wuketao on 2018/1/30.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    /**
     * 通过登录名查找
     *
     * @param loginName
     * @return
     */
    User findByLoginName(String loginName);

    /**
     * 通过登录名和密码查询数据
     *
     * @param loginName
     * @param password
     * @return
     */
    User findByLoginNameAndPassword(String loginName, String password);

    /**
     * 通过 登录名、用户姓名、组织机构编码、组织机构名称 模糊查询分页数据
     * @param loginName
     * @param userName
     * @param orgCode
     * @param orgName
     * @param pageable
     * @return
     */
    Page<User> findByLoginNameLikeOrUserNameLikeOrOrgCodeLikeOrOrgNameLike(
            String loginName, String userName, String orgCode, String orgName, Pageable pageable);
}
