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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import qunar.tc.bistoury.ui.service.URLRedirectService;

/**
 * @author leix.xie
 * @date 2019/7/10 14:38
 * @describe
 */
@Controller
@RequestMapping("api/url/")
public class URLRedirectController {

    @Autowired
    private URLRedirectService urlRedirectService;

    @RequestMapping("redirect")
    public ModelAndView redirect(final String name) {
        try {
            String url = this.urlRedirectService.getURLByName(name);
            return new ModelAndView(new RedirectView(url));
        } catch (Exception e) {
            return new ModelAndView(new RedirectView("/redirectError.html"));
        }
    }
}
