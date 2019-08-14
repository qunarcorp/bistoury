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

package qunar.tc.bistoury.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.model.User;
import qunar.tc.bistoury.ui.security.LoginContext;
import qunar.tc.bistoury.ui.service.LoginManager;
import qunar.tc.bistoury.ui.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhenyu.nie created on 2018 2018/10/24 14:23
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginManager loginManager;

    @RequestMapping("logout")
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) {
        request.removeAttribute(LoginContext.CONTEXT);
        loginManager.logout(response);
        return new ModelAndView(new RedirectView("/login.html"));
    }

    @RequestMapping("login")
    public ModelAndView login(@RequestParam final String userCode, @RequestParam final String password, HttpServletRequest request, HttpServletResponse response) {
        User user = new User(userCode, password);
        if (this.userService.login(user)) {
            loginManager.login(user.getUserCode(), response);
            return new ModelAndView(new RedirectView("/"));
        } else {
            return new ModelAndView(new RedirectView("/login.html?error=-1"));
        }
    }

    @ResponseBody
    @RequestMapping("user/register")
    public ApiResult register(User user) {
        int result = this.userService.register(user);
        if (result == -1) {
            return ResultHelper.fail("用户" + user.getUserCode() + "已存在");
        } else {
            return ResultHelper.success();
        }
    }

}
