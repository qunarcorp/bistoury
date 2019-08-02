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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import qunar.tc.bistoury.serverside.bean.ApiResult;
import qunar.tc.bistoury.serverside.util.ResultHelper;
import qunar.tc.bistoury.ui.exception.SourceFileNotFoundException;
import qunar.tc.bistoury.ui.exception.SourceFileReadException;
import qunar.tc.bistoury.ui.model.MavenInfo;
import qunar.tc.bistoury.ui.service.MavenRepositoryService;

/**
 * @author: leix.xie
 * @date: 2019/4/3 10:45
 * @describe：
 */
@Controller
@RequestMapping("api/maven/repository")
public class MavenRepositoryApiController {
    private static final Logger logger = LoggerFactory.getLogger(MavenRepositoryApiController.class);

    @Autowired
    private MavenRepositoryService mavenRepositoryService;

    @ResponseBody
    @RequestMapping("file")
    public ApiResult getFile(@RequestParam String artifactId, @RequestParam String groupId, @RequestParam String version, @RequestParam String className) {
        MavenInfo mavenInfo = new MavenInfo(artifactId, groupId, version);
        try {
            return ResultHelper.success(0, getFileName(className), this.mavenRepositoryService.getSourceFile(mavenInfo, className));
        } catch (SourceFileNotFoundException e) {
            //可以尝试再次从maven仓库拉取
            logger.error("源文件不存在, mavenInfo: {}, className: {}", mavenInfo, className);
            return ResultHelper.fail(-2, "源码不存在");
        } catch (SourceFileReadException e) {
            logger.error("文件读取失败", e);
            //文件是存在的，但是读取失败了，不用尝试了
            return ResultHelper.fail(-1, "源码读取失败");
        } catch (Exception e) {
            logger.error("源码读取失败", e);
            return ResultHelper.fail(-1, e.getMessage());
        }
    }

    @ResponseBody
    @RequestMapping("downsource")
    public ApiResult downSourceFile(@RequestParam String artifactId, @RequestParam String groupId, @RequestParam String version, @RequestParam String className) {
        MavenInfo mavenInfo = new MavenInfo(artifactId, groupId, version);
        try {
            return ResultHelper.success(0, getFileName(className), this.mavenRepositoryService.downSourceFile(mavenInfo, className));
        } catch (SourceFileNotFoundException e) {
            logger.error("源码下载失败，maven: {}, class name: {}", mavenInfo, className, e);
            return ResultHelper.fail(-1, "源码下载失败");
        } catch (SourceFileReadException ie) {
            logger.error("文件读取失败", ie);
            return ResultHelper.fail(-1, "源码读取失败");
        } catch (Exception e) {
            logger.error("源码下载失败", e);
            Throwable cause = e.getCause();
            if (cause instanceof SourceFileNotFoundException) {
                return ResultHelper.fail(-1, cause.getMessage());
            } else {
                return ResultHelper.fail(-1, e.getMessage());
            }
        }
    }

    private String getFileName(String className) {
        return className.substring(className.lastIndexOf(".") + 1) + ".java";
    }
}