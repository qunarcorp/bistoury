package qunar.tc.bistoury.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.support.Application;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.security.LoginContext;
import qunar.tc.bistoury.ui.service.ApplicationService;

/**
 * @author leix.xie
 * @date 2019/7/2 20:07
 * @describe
 */
@Controller
@RequestMapping("api/application/")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @ResponseBody
    @RequestMapping("list")
    public ApiResult getAppList() {
        String userCode = LoginContext.getLoginContext().getLoginUser();
        return ResultHelper.success(this.applicationService.getAllApplications(userCode));
    }

    @ResponseBody
    @RequestMapping("owner")
    public ApiResult getAppOwner(final String appCode) {
        return ResultHelper.success(this.applicationService.getAppOwner(appCode));
    }

    @ResponseBody
    @RequestMapping("save")
    public ApiResult save(Application application) {
        return ResultHelper.success(this.applicationService.save(application));
    }
}
