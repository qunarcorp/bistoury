package qunar.tc.bistoury.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.configuration.DynamicConfigLoader;

/**
 * @author: leix.xie
 * @date: 2018/11/8 15:12
 * @describe：
 */
@Controller
public class HelpController {

    private static String version;

    static {
        DynamicConfigLoader.load("config.properties").addListener(conf -> version = conf.getString("agent.lastVersion"));
    }

    @ResponseBody
    @RequestMapping("version")
    public Object getLatestVersion() {
        return version;
    }
}
