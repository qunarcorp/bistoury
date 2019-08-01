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
