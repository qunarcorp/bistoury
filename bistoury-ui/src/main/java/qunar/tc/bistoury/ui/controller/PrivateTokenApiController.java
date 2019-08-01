package qunar.tc.bistoury.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.model.PrivateToken;
import qunar.tc.bistoury.ui.security.LoginContext;
import qunar.tc.bistoury.ui.service.GitlabPrivateTokenService;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author keli.wang
 */
@Controller
@RequestMapping("/api/settings/token")
public class PrivateTokenApiController {
    @Resource
    private GitlabPrivateTokenService gitlabPrivateTokenService;


    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult saveToken(@RequestParam final String privateToken) {
        final String username = LoginContext.getLoginContext().getLoginUser();
        final int ret = gitlabPrivateTokenService.saveToken(username, privateToken);
        if (ret > 0) {
            return ResultHelper.success();
        } else {
            return ResultHelper.fail("保存 Gitlab Private Token 失败");
        }
    }

    @RequestMapping("/query")
    @ResponseBody
    public ApiResult<PrivateToken> queryToken() {
        final String userCode = LoginContext.getLoginContext().getLoginUser();
        Optional<PrivateToken> privateToken = gitlabPrivateTokenService.queryToken(userCode);
        if (!privateToken.isPresent()) {
            return ResultHelper.fail(-2, "请先配置private token");
        }
        return ResultHelper.success(privateToken.get());
    }
}
