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

package qunar.tc.bistoury.commands.arthas;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhenyu.nie created on 2018 2018/10/16 17:04
 */
public class ArthasEntity {

    private static final Logger logger = LoggerFactory.getLogger(ArthasEntity.class);

    private volatile int pid;

    private volatile String nullableAppCode;

    public ArthasEntity(String nullableAppCode, int pid) {
        this.pid = pid;
        this.nullableAppCode = nullableAppCode;
    }

    public void start() {
        try {
            ArthasStarter.start(nullableAppCode, pid);
        } catch (Exception e) {
            logger.error("start arthas error, nullableAppCode [{}], pid [{}]", nullableAppCode, pid, e);
            throw new RuntimeException("start arthas error, nullableAppCode [" + Strings.nullToEmpty(nullableAppCode)
                    + " ] " + "pid [" + pid + "]," + e.getMessage());
        }
    }

    public int getPid() {
        return pid;
    }

    public String getNullableAppCode() {
        return nullableAppCode;
    }
}
