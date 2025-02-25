package org.pbms.pbmsserver.service;

import org.pbms.pbmsserver.common.constant.ServerConstant;
import org.pbms.pbmsserver.common.exception.BusinessException;
import org.pbms.pbmsserver.common.exception.BusinessStatus;
import org.pbms.pbmsserver.common.exception.ClientException;
import org.pbms.pbmsserver.dao.SystemDao;
import org.pbms.pbmsserver.dao.UserInfoDao;
import org.pbms.pbmsserver.dao.UserSettingsDao;
import org.pbms.pbmsserver.repository.enumeration.user.UserStatusEnum;
import org.pbms.pbmsserver.repository.mapper.UserInfoDynamicSqlSupport;
import org.pbms.pbmsserver.repository.model.UserInfo;
import org.pbms.pbmsserver.repository.model.UserSettings;
import org.pbms.pbmsserver.service.common.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import static org.mybatis.dynamic.sql.SqlBuilder.isEqualTo;
import static org.mybatis.dynamic.sql.SqlBuilder.isIn;

/**
 * @author zyl
 */
@Service
public class UserManageService {
    private static final Logger log = LoggerFactory.getLogger(UserManageService.class);

    @Autowired
    private UserInfoDao userInfoDao;

    @Autowired
    private UserSettingsDao userSettingsDao;

    @Autowired
    private SystemDao systemDao;

    @Autowired
    private MailService mailService;

    @Transactional
    public void updateStatus(long userId, UserStatusEnum status) {
        log.debug("userId : {}, status : {}", userId, status);
        int count = this.userInfoDao.update(c -> c
                .set(UserInfoDynamicSqlSupport.status).equalTo(status::getCode)
                .where(UserInfoDynamicSqlSupport.userId, isEqualTo(userId))
                .and(UserInfoDynamicSqlSupport.status, isIn(UserStatusEnum.getLegalStatus(status)))
        );
        if (count == 0) {
            log.debug("legalStatus:{}", UserStatusEnum.getLegalStatus(status));
            throw new ClientException("操作失败");
        }
    }

    @Transactional
    public void deleteUser(long userId) {
        userInfoDao.selectByPrimaryKey(userId).orElseThrow(() -> new ClientException("用户错误"));
        // 删除用户信息
        this.userInfoDao.deleteByPrimaryKey(userId);
        // 删除用户配置
        this.userSettingsDao.deleteByPrimaryKey(userId);
        // 删除用户文件
        this.systemDao.removeAllRespectiveDir(userId);
    }

    @Transactional
    public void auditUser(long userId, boolean optional) {
        UserInfo user = this.userInfoDao.selectOne(c -> c
                .where(UserInfoDynamicSqlSupport.userInfo.userId, isEqualTo(userId))
                .and(UserInfoDynamicSqlSupport.userInfo.status, isIn(UserStatusEnum.getLegalStatus(UserStatusEnum.NORMAL)))
        ).orElseThrow(() -> new BusinessException(BusinessStatus.USER_NOT_FOUND));

        UserInfo updateUser = new UserInfo();
        updateUser.setUserId(userId);
        if (optional) {
            updateUser.setStatus(UserStatusEnum.NORMAL.getCode());

            // 初始化默认设置
            this.initDefaultSettings(userId);

            // 初始化存储路径
            this.systemDao.initAllRespectiveDir(user);

            Context context = new Context();
            context.setVariable("name", user.getUserName());
            mailService.sendMail(ServerConstant.ACCEPT_SUBJECT, user.getEmail(), ServerConstant.ACCEPT_TEMPLATE, context);
        } else {
            updateUser.setStatus(UserStatusEnum.AUDIT_FAIL.getCode());
        }
        // 更新用户状态
        this.userInfoDao.updateByPrimaryKeySelective(updateUser);
    }

    public void initDefaultSettings(long userId) {
        UserSettings userSettings = new UserSettings();
        userSettings.setUserId(userId);
        userSettings.setWatermarkLogoEnable(false);
        userSettings.setWatermarkTextEnable(false);
        userSettings.setCompressScale((byte) 0);
        userSettings.setResponseReturnType("markdown");
        this.userSettingsDao.insert(userSettings);
    }
}
