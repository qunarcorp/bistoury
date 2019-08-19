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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhenyu.nie created on 2019 2019/1/10 16:18
 */
public class DefaultMetaStore implements MetaStore {

    private volatile Map<String, String> attrs = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(DefaultMetaStore.class);

    public DefaultMetaStore() {
    }

    public DefaultMetaStore(Map<String, String> attrs) {
        this.attrs = attrs;
    }

    @Override
    public void update(Map<String, String> attrs) {
        logger.debug("update agent info, {}", attrs);
        this.attrs = attrs;
    }

    @Override
    public void put(String key, String value) {
        this.attrs.put(key, value);
    }

    @Override
    public Map<String, String> getAgentInfo() {
        return Collections.unmodifiableMap(this.attrs);
    }

    @Override
    public String getStringProperty(String name) {
        return getStringProperty(name, null);
    }

    @Override
    public String getStringProperty(String name, String def) {
        String v = attrs.get(name);
        if (v == null) {
            return def;
        }
        return v;
    }

    @Override
    public boolean getBooleanProperty(String name) {
        return getBooleanProperty(name, false);
    }

    @Override
    public boolean getBooleanProperty(String name, boolean def) {
        String v = attrs.get(name);
        if (v == null || v.isEmpty()) {
            return def;
        }
        return Boolean.parseBoolean(v.trim());
    }

    @Override
    public Date getDateProperty(String name) {
        String o = attrs.get(name);
        if (o == null) {
            return null;
        }
        Long v = Long.valueOf(o);
        return new Date(v);
    }

    @Override
    public Integer getIntegerProperty(String name) {
        String value = attrs.get(name);
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value);
    }

    @Override
    public int getIntProperty(String name) {
        return getIntProperty(name, 0);
    }

    @Override
    public int getIntProperty(String name, int def) {
        String o = attrs.get(name);
        return Numbers.toInt(o, def);
    }

    @Override
    public long getLongProperty(String name) {
        return getLongProperty(name, 0);
    }

    @Override
    public long getLongProperty(String name, long def) {
        String o = attrs.get(name);
        return Numbers.toLong(o, def);
    }

    @Override
    public float getFloatProperty(String name) {
        return getFloatProperty(name, 0);
    }

    @Override
    public float getFloatProperty(String name, float def) {
        String o = attrs.get(name);
        return Numbers.toFloat(o, def);
    }

    @Override
    public double getDoubleProperty(String name) {
        return getDoubleProperty(name, 0);
    }

    @Override
    public double getDoubleProperty(String name, double def) {
        String o = attrs.get(name);
        return Numbers.toDouble(o, def);
    }
}
