/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.ui.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import qunar.tc.bistoury.serverside.support.Application;
import qunar.tc.bistoury.ui.dao.ApplicationDao;
import qunar.tc.bistoury.ui.dao.ApplicationUserDao;
import qunar.tc.bistoury.ui.exception.PermissionDenyException;
import qunar.tc.bistoury.ui.security.LoginContext;
import qunar.tc.bistoury.ui.service.ApplicationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author leix.xie
 * @date 2019/7/2 20:14
 * @describe
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private ApplicationUserDao applicationUserDao;

    @Override
    public List<Application> getAllApplications(final String userCode) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(userCode), "user code cannot be null or empty");
        List<String> appCodes = this.applicationUserDao.getAppCodesByUserCode(userCode);
        List<Application> applications = this.applicationDao.getApplicationsByAppCodes(appCodes);
        return applications;
    }

    @Override
    public List<String> getAppOwner(final String appCode) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(appCode), "app code cannot be null or empty");
        return this.applicationUserDao.getUsersByAppCode(appCode);
    }

    @Override
    @Transactional
    public int save(Application application) {
        String appCode = application.getCode();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(appCode), "app code cannot be null or empty");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(application.getOwner()), "owner cannot be null or empty");
        Application oldApplication = this.applicationDao.getApplicationByAppCode(appCode);

        if (application.getId() <= 0) {
            if (oldApplication != null) {
                throw new RuntimeException("应用新增失败，应用" + appCode + "已存在");
            }
            final String userCode = LoginContext.getLoginContext().getLoginUser();
            application.setCreator(userCode);
            application.setCreateTime(new Date());
            List<String> owner = application.getOwner();
            if (!owner.contains(userCode)) {
                owner.add(userCode);
            }
            this.applicationUserDao.batchAddAppUser(owner, appCode);
            logger.info("{} add application {}", userCode, application);
            return this.applicationDao.createApplication(application);
        } else {
            if (oldApplication == null) {
                throw new RuntimeException("数据错误");
            }
            verification(oldApplication);

            //去除与新owner的交集后剩余的需要删除
            List<String> oldOwners = this.getAppOwner(appCode);
            ArrayList<String> oldOwnersCopy = Lists.newArrayList(oldOwners);
            //去除与老的owner的交集后需要新增
            List<String> newOwners = application.getOwner();

            logger.info("{} update application {}, owner {} to {}", LoginContext.getLoginContext().getLoginUser(), application, oldOwners, newOwners);

            oldOwners.removeAll(newOwners);
            newOwners.removeAll(oldOwnersCopy);
            for (String owner : oldOwners) {
                this.applicationUserDao.removeAppUser(owner, appCode);
            }
            this.applicationUserDao.batchAddAppUser(newOwners, appCode);
            return this.applicationDao.updateApplication(application);
        }
    }

    private boolean verification(Application application) {
        String userCode = LoginContext.getLoginContext().getLoginUser();
        List<String> owner = this.getAppOwner(application.getCode());
        if (owner.contains(userCode) || LoginContext.getLoginContext().isAdmin()) {
            return true;
        } else {
            throw new PermissionDenyException("仅应用负责人可修改应用信息");
        }
    }
}
