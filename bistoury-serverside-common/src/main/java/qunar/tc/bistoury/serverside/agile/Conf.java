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

package qunar.tc.bistoury.serverside.agile;

import java.util.Map;

/**
 * @author miao.yang susing@gmail.com
 * @since 14-6-5.
 */
public abstract class Conf {

    public static Conf fromMap(final Map<String, String> map) {

        return new Conf() {
            @Override
            protected String getProperty(String key) {
                return map.get(key);
            }
        };
    }

    protected abstract String getProperty(String key);

    public String getString(String name, String def) {
        String v = getProperty(name);
        return v == null ? def : v;
    }

    public int getInt(String name, int def) {
        return Numbers.toInt(getProperty(name), def);
    }

    public long getLong(String name, long def) {
        return Numbers.toLong(getProperty(name), def);
    }

    public float getFloat(String name, float def) {
        return Numbers.toFloat(getProperty(name), def);
    }

    public double getDouble(String name, double def) {
        return Numbers.toDouble(getProperty(name), def);
    }

    public boolean getBoolean(String name, boolean def) {
        return Strings.getBoolean(getProperty(name), def);
    }


}
