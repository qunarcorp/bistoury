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

package qunar.tc.bistoury.clientside.common.meta;

import java.util.Date;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2019 2019/1/10 15:44
 */
public interface MetaStore {

    void update(Map<String, String> attrs);

    Map<String, String> getAgentInfo();

    String getStringProperty(String name);

    String getStringProperty(String name,String def);

    boolean getBooleanProperty(String name);

    boolean getBooleanProperty(String name, boolean def);

    Date getDateProperty(String name);

    int getIntProperty(String name);

    int getIntProperty(String name, int def);

    long getLongProperty(String name);

    long getLongProperty(String name, long def);

    float getFloatProperty(String name);

    float getFloatProperty(String name, float def);

    double getDoubleProperty(String name);

    double getDoubleProperty(String name, double def);
}
