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

import java.util.List;
import java.util.ServiceLoader;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import qunar.tc.bistoury.serverside.configuration.DynamicConfig;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;
import qunar.tc.bistoury.serverside.configuration.local.LocalDynamicConfig;
import qunar.tc.bistoury.ui.dao.UserDao;
import qunar.tc.bistoury.ui.model.User;
import qunar.tc.bistoury.ui.service.UserService;

/**
 * @author leix.xie
 * @date 2019/7/4 11:00
 * @describe
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();

    /**
     * 密码加密器,可通过环境变理BISTOURY_UI_PASSWORD_ENCODER来指定实现类,
     * 也可用new表示采用推存的加密实现类:Pbkdf2PasswordEncoder
     * 用old表示旧实现PasswordEncoderForOldAESCryptImpl
     * 如未指定默认采用SPI方式,且SPI默认实现为旧的PasswordEncoderForOldAESCryptImpl
     */
    private static final PasswordEncoder PASSWORD_ENCODER;
    static {
       String cls = System.getenv("BISTOURY_UI_PASSWORD_ENCODER");
       if ("new".equals(cls)) {
           PASSWORD_ENCODER = new Pbkdf2PasswordEncoder();
       } else if ("old".equals(cls)) {
           PASSWORD_ENCODER = new PasswordEncoderForOldAESCryptImpl();
       } else if (cls != null) {
           ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
           try {
               PASSWORD_ENCODER = (PasswordEncoder)classLoader.loadClass(cls).newInstance();
           } catch (InstantiationException e) {
               throw new IllegalStateException(e);
           } catch (IllegalAccessException e) {
               throw new IllegalStateException(e);
           } catch (ClassNotFoundException e) {
               throw new IllegalStateException(e);
           }
       } else {
           ServiceLoader<PasswordEncoder> sloader = ServiceLoader.load(PasswordEncoder.class);
           PASSWORD_ENCODER = sloader.iterator().next();
       }
    }

    public static String encodePwd(String rawPassword) {
        return PASSWORD_ENCODER.encode(rawPassword);
    }
    
    @Autowired
    private UserDao userDao;

    private List<String> admins;

    @PostConstruct
    public void init() {
        DynamicConfig<LocalDynamicConfig> dynamicConfig = DynamicConfigLoader.load("config.properties");
        dynamicConfig.addListener(conf -> admins = SPLITTER.splitToList(conf.getString("admins", "")));
    }

    @Override
    public boolean login(User user) {
        User checkUser = this.userDao.getUserByUserCode(user.getUserCode());
        if (checkUser == null || !PASSWORD_ENCODER.matches(user.getPassword(), checkUser.getPassword())) {
            return false;
        }
        return true;
    }

    /**
     * 为true表示禁止帐号注册功能
     */
    private final boolean disableRegister = Boolean.getBoolean("bistoury.ui.register_disabled");

    @Override
    public int register(User user) {
        if (disableRegister) {
            throw new IllegalAccessError("User register is disabled!");
        }
        Preconditions.checkNotNull(user, "user cannot be null");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getUserCode()), "user code cannot be null or empty");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getPassword()), "password cannot be null or empty");
        User checkUser = this.userDao.getUserByUserCode(user.getUserCode());
        if (checkUser != null) {
            return -1;
        }
        user.setPassword(PASSWORD_ENCODER.encode(user.getPassword()));
        return this.userDao.registerUser(user);
    }

    @Override
    public boolean isAdmin(final String userCode) {
        return admins.contains(userCode);
    }
}
