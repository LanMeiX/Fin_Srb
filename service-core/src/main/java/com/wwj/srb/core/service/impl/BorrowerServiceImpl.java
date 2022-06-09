package com.wwj.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wwj.srb.core.enums.BorrowerStatusEnum;
import com.wwj.srb.core.enums.IntegralEnum;
import com.wwj.srb.core.mapper.BorrowerAttachMapper;
import com.wwj.srb.core.mapper.BorrowerMapper;
import com.wwj.srb.core.mapper.UserInfoMapper;
import com.wwj.srb.core.mapper.UserIntegralMapper;
import com.wwj.srb.core.pojo.entity.Borrower;
import com.wwj.srb.core.pojo.entity.BorrowerAttach;
import com.wwj.srb.core.pojo.entity.UserInfo;
import com.wwj.srb.core.pojo.entity.UserIntegral;
import com.wwj.srb.core.pojo.vo.BorrowerApprovalVO;
import com.wwj.srb.core.pojo.vo.BorrowerAttachVO;
import com.wwj.srb.core.pojo.vo.BorrowerDetailVO;
import com.wwj.srb.core.pojo.vo.BorrowerVO;
import com.wwj.srb.core.service.BorrowerAttachService;
import com.wwj.srb.core.service.BorrowerService;
import com.wwj.srb.core.service.DictService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 借款人 服务实现类
 * </p>
 *
 * @author wangweijun
 * @since 2021-04-27
 */
@Service
public class BorrowerServiceImpl extends ServiceImpl<BorrowerMapper, Borrower> implements BorrowerService {

    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private BorrowerAttachMapper borrowerAttachMapper;
    @Autowired
    private DictService dictService;
    @Autowired
    private BorrowerAttachService borrowerAttachService;
    @Resource
    private UserIntegralMapper userIntegralMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveBorrowerVOByUserId(BorrowerVO borrowerVO, Long userId) {
        // 获取用户基本信息
        UserInfo userInfo = userInfoMapper.selectById(userId);

        // 保存借款人信息
        Borrower borrower = new Borrower();
        BeanUtils.copyProperties(borrowerVO, borrower);

        borrower.setUserId(userId);
        borrower.setName(userInfo.getName());
        borrower.setIdCard(userInfo.getIdCard());
        borrower.setMobile(userInfo.getMobile());
        // 设置状态为认证中
        borrower.setStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        baseMapper.insert(borrower);

        // 保存附件
        List<BorrowerAttach> borrowerAttachList = borrowerVO.getBorrowerAttachList();
        borrowerAttachList.forEach(borrowerAttach -> {
            borrowerAttach.setBorrowerId(borrower.getId());
            borrowerAttachMapper.insert(borrowerAttach);
        });

        // 更新user_info表中的借款人认证状态
        userInfo.setBorrowAuthStatus(BorrowerStatusEnum.AUTH_RUN.getStatus());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public Integer getStatusByUserId(Long userId) {
        List<Object> statusList = baseMapper.selectObjs(
                new LambdaQueryWrapper<Borrower>()
                        .select(Borrower::getStatus)
                        .eq(Borrower::getUserId, userId));
        if (statusList.size() == 0) {
            return BorrowerStatusEnum.NO_AUTH.getStatus();
        }
        return (Integer) statusList.get(0);
    }

    @Override
    public IPage<Borrower> listPage(Page<Borrower> pageParam, String keyword) {
        // 判断是否存在查询条件
        if (StringUtils.isBlank(keyword)) {
            return baseMapper.selectPage(pageParam, null);
        }
        return baseMapper.selectPage(pageParam,
                new LambdaQueryWrapper<Borrower>()
                        .like(Borrower::getName, keyword)
                        .or().like(Borrower::getIdCard, keyword)
                        .or().like(Borrower::getMobile, keyword)
                        .orderByDesc(Borrower::getId));
    }

    @Override
    public BorrowerDetailVO getBorrowerDetailVOById(Long id) {
        // 获取借款人信息
        Borrower borrower = baseMapper.selectById(id);
        // 填充基本借款人信息
        BorrowerDetailVO borrowerDetailVO = new BorrowerDetailVO();
        BeanUtils.copyProperties(borrower, borrowerDetailVO);
        // 婚否
        borrowerDetailVO.setMarry(borrower.getMarry() ? "是" : "否");
        // 性别
        borrowerDetailVO.setSex(borrower.getSex() == 1 ? "男" : "女");
        // 下拉列表
        borrowerDetailVO.setEducation(dictService.getNameByParentDictCodeAndValue("education", borrower.getEducation()));
        borrowerDetailVO.setIndustry(dictService.getNameByParentDictCodeAndValue("industry", borrower.getIndustry()));
        borrowerDetailVO.setIncome(dictService.getNameByParentDictCodeAndValue("income", borrower.getIncome()));
        borrowerDetailVO.setReturnSource(dictService.getNameByParentDictCodeAndValue("returnSource", borrower.getReturnSource()));
        borrowerDetailVO.setContactsRelation(dictService.getNameByParentDictCodeAndValue("relation", borrower.getContactsRelation()));
        // 认证状态
        Integer status = borrower.getStatus();
        borrowerDetailVO.setStatus(BorrowerStatusEnum.getMsgByStatus(status));
        // 附件列表
        List<BorrowerAttachVO> borrowerAttachVOList = borrowerAttachService.selectBorrowerAttachVOList(id);
        borrowerDetailVO.setBorrowerAttachVOList(borrowerAttachVOList);
        return borrowerDetailVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void approval(BorrowerApprovalVO borrowerApprovalVO) {
        // 获取借款额度申请id
        Long borrowerId = borrowerApprovalVO.getBorrowerId();
        // 获取借款额度申请对象
        Borrower borrower = baseMapper.selectById(borrowerId);
        // 设置审核状态
        borrower.setStatus(borrowerApprovalVO.getStatus());
        baseMapper.updateById(borrower);

        // 获取会员id
        Long userId = borrower.getUserId();
        // 获取会员对象
        UserInfo userInfo = userInfoMapper.selectById(userId);
        // 获取会员原始积分
        Integer integral = userInfo.getIntegral();

        // 设置会员基本信息积分
        UserIntegral userIntegral = new UserIntegral();
        userIntegral.setUserId(userId);
        userIntegral.setIntegral(borrowerApprovalVO.getInfoIntegral());
        userIntegral.setContent("借款人基本信息");
        userIntegralMapper.insert(userIntegral);
        int currentIntegral = integral + borrowerApprovalVO.getInfoIntegral();

        // 设置附件积分（身份证）
        if (borrowerApprovalVO.getIsIdCardOk()) {
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_IDCARD.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_IDCARD.getMsg());
            userIntegralMapper.insert(userIntegral);
            currentIntegral += IntegralEnum.BORROWER_IDCARD.getIntegral();
        }
        // 设置附件积分（房产）
        if (borrowerApprovalVO.getIsHouseOk()) {
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_HOUSE.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_HOUSE.getMsg());
            userIntegralMapper.insert(userIntegral);
            currentIntegral += IntegralEnum.BORROWER_HOUSE.getIntegral();
        }
        // 设置附件积分（车辆）
        if (borrowerApprovalVO.getIsCarOk()) {
            userIntegral = new UserIntegral();
            userIntegral.setUserId(userId);
            userIntegral.setIntegral(IntegralEnum.BORROWER_CAR.getIntegral());
            userIntegral.setContent(IntegralEnum.BORROWER_CAR.getMsg());
            userIntegralMapper.insert(userIntegral);
            currentIntegral += IntegralEnum.BORROWER_CAR.getIntegral();
        }
        // 设置总积分
        userInfo.setIntegral(currentIntegral);
        // 修改审核状态
        userInfo.setBorrowAuthStatus(borrowerApprovalVO.getStatus());
        // 更新userinfo
        userInfoMapper.updateById(userInfo);
    }
}
