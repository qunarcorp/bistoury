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

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.ui.service.LoginManager;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author leix.xie
 * @date 2019/7/4 11:44
 * @describe
 */
@Service
public class BistouryLoginManager implements LoginManager {
    private final Logger logger = LoggerFactory.getLogger(BistouryLoginManager.class);

    private String appCode = "";
    private int timeout = 24 * 60 * 60; // cookie time out

    private String cookieId = "login_id";
    private String cookieTime = "login_time";
    private String cookieToken = "login_token";

    public void login(String loginId, HttpServletResponse response) {

        String _time = String.valueOf(System.currentTimeMillis());

        // set-cookie
        // loginId, time, token
        Cookie id = new Cookie(cookieId, loginId);
        Cookie time = new Cookie(cookieTime, String.valueOf(_time));
        Cookie token = new Cookie(cookieToken, token(loginId, _time));

        id.setPath("/");
        time.setPath("/");
        token.setPath("/");

        id.setMaxAge(timeout);
        time.setMaxAge(timeout);
        token.setMaxAge(timeout);

        response.addCookie(id);
        response.addCookie(time);
        response.addCookie(token);
    }

    public boolean isLogin(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return false;
        }

        String id = null;
        String time = null;
        String token = null;

        for (Cookie cookie : cookies) {
            String name = cookie.getName();
            if (cookieId.equals(name)) {
                id = cookie.getValue();
            } else if (cookieTime.equals(name)) {
                time = cookie.getValue();
            } else if (cookieToken.equals(name)) {
                token = cookie.getValue();
            }
        }

        logger.debug("id={}, time={}, token={}", id, time, token);

        if (Strings.isNullOrEmpty(id) || Strings.isNullOrEmpty(time) || Strings.isNullOrEmpty(token)) {
            return false;
        }

        return token.equals(token(id, time));
    }

    public String token(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookieToken.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;

    }

    private String token(String id, String time) {
        return Hashing.md5().hashString(id + "_" + time, Charsets.UTF_8).toString();
    }

    public String current(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieId.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public void logout(HttpServletResponse response) {
        Cookie id = new Cookie(cookieId, null);
        Cookie time = new Cookie(cookieTime, null);
        Cookie token = new Cookie(cookieToken, null);

        id.setPath("/");
        time.setPath("/");
        token.setPath("/");

        id.setMaxAge(0);
        time.setMaxAge(0);
        token.setMaxAge(0);

        response.addCookie(id);
        response.addCookie(time);
        response.addCookie(token);
    }

    @PostConstruct
    public void init() {

        if (Strings.isNullOrEmpty(appCode)) {
            return;
        }

        cookieId = appCode + "_" + cookieId;
        cookieTime = appCode + "_" + cookieTime;
        cookieToken = appCode + "_" + cookieToken;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
