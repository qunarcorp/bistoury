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

package qunar.tc.bistoury.ui.model;

/**
 * @author leix.xie
 * @date 2019/7/10 10:20
 * @describe
 */
public class ReleaseInfo {
    /**
     * 发布项目名
     */
    private String project;
    /**
     * 应用所在module，没有时，module为一个英文句号[.]
     */
    private String module;
    /**
     * 发布的版本号/分支/tag
     */
    private String output;

    public ReleaseInfo() {
    }

    public ReleaseInfo(String project, String module, String output) {
        this.project = project;
        this.module = module;
        this.output = output;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return "ReleaseInfo{" +
                "project='" + project + '\'' +
                ", module='" + module + '\'' +
                ", output='" + output + '\'' +
                '}';
    }
}
