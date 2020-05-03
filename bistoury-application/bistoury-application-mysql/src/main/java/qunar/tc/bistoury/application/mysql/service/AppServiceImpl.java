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

package qunar.tc.bistoury.application.mysql.service;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.application.api.AppService;
import qunar.tc.bistoury.application.api.pojo.Application;
import qunar.tc.bistoury.application.mysql.dao.ApplicationDao;
import qunar.tc.bistoury.application.mysql.dao.ApplicationUserDao;

import java.util.List;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2018 2018/10/31 13:56
 */
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private ApplicationUserDao applicationUserDao;

    @Override
    public Set<String> getApps(String userCode) {
        List<String> appCodes = this.applicationUserDao.getAppCodesByUserCode(userCode);
        return Sets.newHashSet(appCodes);
    }

    @Override
    public Application getAppInfo(String appCode) {
        return this.applicationDao.getApplicationByAppCode(appCode);
    }

    @Override
    public boolean checkUserPermission(final String appCode, final String usercode) {
        List<String> users = this.applicationUserDao.getUsersByAppCode(appCode);
        return users != null && users.contains(usercode);
    }

}
