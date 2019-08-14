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

package qunar.tc.bistoury.application.mysql.dao;

import qunar.tc.bistoury.application.api.pojo.Application;

import java.util.List;

/**
 * @author leix.xie
 * @date 2019/7/2 10:46
 * @describe
 */
public interface ApplicationDao {
    int updateApplication(Application application);

    Application getApplicationByAppCode(String appCode);

    List<Application> getApplicationsByAppCodes(List<String> appCodes);

    int createApplication(Application application);

    List<Application> getAllApplications();

    boolean checkExist(String appCode);
}
