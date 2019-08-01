package qunar.tc.bistoury.ui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.service.ReleaseInfoService;

/**
 * @author leix.xie
 * @date 2019/7/10 10:19
 * @describe
 */
@Controller
@RequestMapping("api/release/info/")
public class ReleaseInfoController {

    @Autowired
    private ReleaseInfoService releaseInfoService;

    @ResponseBody
    @RequestMapping("parse")
    public ApiResult parseReleaseInfo(final String content) {
        return ResultHelper.success(this.releaseInfoService.parseReleaseInfo(content));
    }

    @ResponseBody
    @RequestMapping("path")
    public ApiResult defaultReleaseInfoPath() {
        return ResultHelper.success(this.releaseInfoService.getDefaultReleaseInfoPath());
    }
}
