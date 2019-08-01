package qunar.tc.bistoury.ui.controller;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.remoting.protocol.ErrorCode;
import qunar.tc.bistoury.serverside.util.ResultHelper;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author leix.xie
 * @date 2019/6/10 11:21
 * @describe
 */
@Controller
public class ErrorCodeMappingController {

    @ResponseBody
    @RequestMapping("api/errorcode/mapping")
    public Object getErrorCodeMapping() {
        ArrayList<ErrorCode> errorCodes = Lists.newArrayList(ErrorCode.values());
        Map<Integer, String> errorCodeMapping = errorCodes.stream().collect(Collectors.toMap(ErrorCode::getCode, ErrorCode::getMessage));
        return ResultHelper.success(errorCodeMapping);
    }
}
