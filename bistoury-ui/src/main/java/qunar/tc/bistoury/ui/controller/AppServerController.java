package qunar.tc.bistoury.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.support.AppServer;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.service.AppServerService;

/**
 * @author leix.xie
 * @date 2019/7/3 16:35
 * @describe
 */
@Controller
@RequestMapping("api/app/server/")
public class AppServerController {

    @Autowired
    private AppServerService appServerService;

    @ResponseBody
    @RequestMapping("list")
    public ApiResult getAppServerByAppCode(final String appCode) {
        return ResultHelper.success(this.appServerService.getAppServerByAppCode(appCode));
    }

    @ResponseBody
    @RequestMapping("autoJMapHistoEnable")
    public ApiResult changeAutoJMapHistoEnable(final String serverId, final boolean enable) {
        return ResultHelper.success(this.appServerService.changeAutoJMapHistoEnable(serverId, enable));
    }

    @ResponseBody
    @RequestMapping("autoJStackEnable")
    public ApiResult changeAutoJStackEnable(final String serverId, final boolean enable) {
        return ResultHelper.success(this.appServerService.changeAutoJStackEnable(serverId, enable));
    }

    @ResponseBody
    @RequestMapping("delete")
    public ApiResult deleteAppServerByServerId(final String serverId) {
        return ResultHelper.success(this.appServerService.deleteAppServerByServerId(serverId));
    }

    @ResponseBody
    @RequestMapping("save")
    public ApiResult saveAppServer(final AppServer appServer) {
        return ResultHelper.success(this.appServerService.saveAppServer(appServer));
    }
}
