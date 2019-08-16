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
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;
import qunar.tc.bistoury.ui.dao.UserDao;
import qunar.tc.bistoury.ui.model.User;
import qunar.tc.bistoury.ui.service.AESCryptService;
import qunar.tc.bistoury.ui.service.UserService;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author leix.xie
 * @date 2019/7/4 11:00
 * @describe
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    @Autowired
    private UserDao userDao;

    @Autowired
    private AESCryptService aesCryptService;

    private List<String> admins;

    @PostConstruct
    public void init() {
        DynamicConfig<LocalDynamicConfig> dynamicConfig = DynamicConfigLoader.load("config.properties");
        dynamicConfig.addListener(conf -> admins = SPLITTER.splitToList(conf.getString("admins", "")));
    }

    @Override
    public boolean login(User user) {
        User checkUser = this.userDao.getUserByUserCode(user.getUserCode());
        if (checkUser == null || !this.aesCryptService.encrypt(user.getPassword()).equals(checkUser.getPassword())) {
            return false;
        }
        return true;
    }

    @Override
    public int register(User user) {
        Preconditions.checkNotNull(user, "user cannot be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getUserCode()), "user code cannot be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getPassword()), "password cannot be null or empty");
        User checkUser = this.userDao.getUserByUserCode(user.getUserCode());
        if (checkUser != null) {
            return -1;
        }
        user.setPassword(this.aesCryptService.encrypt(user.getPassword()));
        return this.userDao.registerUser(user);
    }

    @Override
    public boolean isAdmin(final String userCode) {
        return admins.contains(userCode);
    }
}
