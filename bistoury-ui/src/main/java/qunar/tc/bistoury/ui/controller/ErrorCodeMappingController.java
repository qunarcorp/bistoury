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
