package qunar.tc.bistoury.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.bean.ApiStatus;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.security.LoginContext;
import qunar.tc.bistoury.ui.service.AdminAppService;
import qunar.tc.bistoury.ui.service.AppServerService;
import qunar.tc.bistoury.ui.service.AppService;

@Controller
public class AppController {

    private static final int ADMIN_PAGE_SIZE = 20;

    @Autowired
    private AppService appService;

    @Autowired
    private AdminAppService adminAppService;

    @Autowired
    private AppServerService appServerService;

    @RequestMapping("getApps")
    @ResponseBody
    public ApiResult getApps() {
        try {
            final String userName = LoginContext.getLoginContext().getLoginUser();
            return ResultHelper.success(appService.getApps(userName));
        } catch (Exception e) {
            return ResultHelper.fail(-1, "获取应用列表失败");
        }
    }

    @RequestMapping("isAdmin")
    @ResponseBody
    public ApiResult isAdmin() {
        String userName = LoginContext.getLoginContext().getLoginUser();
        return ResultHelper.success(adminAppService.isAdminUser(userName));
    }

    @RequestMapping("searchApps")
    @ResponseBody
    public ApiResult searchApps(String searchAppKey) {
        String userName = LoginContext.getLoginContext().getLoginUser();
        if (adminAppService.isAdminUser(userName)) {
            return ResultHelper.success(adminAppService.searchApps(searchAppKey, ADMIN_PAGE_SIZE));
        } else {
            return ResultHelper.fail(ApiStatus.PERMISSION_DENY.getCode(), ApiStatus.PERMISSION_DENY.getMsg());
        }
    }

    @RequestMapping("getHosts")
    @ResponseBody
    public ApiResult getHosts(@RequestParam(name = "appCode") String appCode) {
        return ResultHelper.success(this.appServerService.getAppServerByAppCode(appCode));
    }

    @ResponseBody
    @RequestMapping("getAppInfo")
    public ApiResult getAppInfo(@RequestParam("appCode") String appCode) {
        return ResultHelper.success(this.appService.getAppInfo(appCode));
    }

}
