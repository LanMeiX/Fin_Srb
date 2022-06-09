package com.wwj.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wwj.srb.core.pojo.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wwj.srb.core.pojo.query.UserInfoQuery;
import com.wwj.srb.core.pojo.vo.LoginVO;
import com.wwj.srb.core.pojo.vo.RegisterVO;
import com.wwj.srb.core.pojo.vo.UserIndexVO;
import com.wwj.srb.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author wangweijun
 * @since 2021-04-27
 */
public interface UserInfoService extends IService<UserInfo> {

    /**
     * 完成用户注册
     *
     * @param registerVO 注册信息
     */
    void register(RegisterVO registerVO);

    /**
     * 实现用户登录
     *
     * @param loginVO 用户输入
     * @param ip      登录ip
     * @return 用户信息对象
     */
    UserInfoVO login(LoginVO loginVO, String ip);


    IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery);

    /**
     * 实现用户锁定
     *
     * @param id     待锁定的用户id
     * @param status
     */
    void lock(Long id, Integer status);

    /**
     * 校验手机号是否已经被注册
     *
     * @param mobile 手机号
     * @return 返回是否被注册，true：已经被注册；false：未被注册
     */
    boolean checkMobile(String mobile);

    UserIndexVO getIndexUserInfo(Long userId);

    String getMobileByBindCode(String bindCode);
}
