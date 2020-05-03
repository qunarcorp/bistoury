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

package qunar.tc.bistoury.ui.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.bean.ApiStatus;
import qunar.tc.bistoury.serverside.exception.PermissionDenyException;
import qunar.tc.bistoury.serverside.util.ResultHelper;

/**
 * @author leix.xie
 * @date 2019/7/2 16:05
 * @describe
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResult runtimeExHandler(Exception e) {
        LOGGER.error("发生系统异常：{}", e.getMessage(), e);
        return ResultHelper.fail(ApiStatus.SYSTEM_ERROR.getCode(), "系统异常：" + e.getMessage());
    }

    @ExceptionHandler(PermissionDenyException.class)
    @ResponseBody
    public ApiResult PermissionDenyExHandler(Exception e) {
        LOGGER.error("权限异常：{}", e.getMessage(), e);
        return ResultHelper.fail(ApiStatus.SYSTEM_ERROR.getCode(), "权限不足，拒绝访问：" + e.getMessage());
    }
}
