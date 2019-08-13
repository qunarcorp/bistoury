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

package qunar.tc.bistoury.ui.security;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import qunar.tc.bistoury.serverside.agile.Strings;
import qunar.tc.bistoury.ui.service.LoginManager;
import qunar.tc.bistoury.ui.service.impl.BistouryLoginManager;
import qunar.tc.bistoury.ui.service.impl.UserServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author leix.xie
 * @date 2019/7/4 11:35
 * @describe
 */
public class LoginInterceptorImpl implements LoginInterceptor {

    private LoginManager loginManager;

    private UserServiceImpl userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean isLogin = loginManager.isLogin(request);
        if (isLogin) {
            setLoginContext(loginManager.current(request), request, response);
            return true;
        } else {
            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                    + request.getContextPath();
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                // 告诉ajax我是重定向
                response.setHeader("REDIRECT", "REDIRECT");
                // 告诉ajax我重定向的路径
                response.setHeader("REDIRECT_PATH", basePath + "/login.html");
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
            } else {
                response.sendRedirect(basePath + "/login.html");
            }
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private void setLoginContext(final String userCode, HttpServletRequest request, HttpServletResponse response) {
        LoginContext loginContext = new LoginContext();
        loginContext.setLoginUser(userCode);
        StringBuffer buffer = request.getRequestURL();
        if (!Strings.isEmpty(request.getQueryString())) {
            buffer.append("?").append(request.getQueryString());
        }
        loginContext.setReturnUrl(buffer.toString());
        loginContext.setRemoteIP(request.getRemoteHost());
        loginContext.setAdmin(this.userService.isAdmin(userCode));
        request.setAttribute(LoginContext.CONTEXT, loginContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        loginManager = applicationContext.getBean(BistouryLoginManager.class);
        userService = applicationContext.getBean(UserServiceImpl.class);
    }
}
