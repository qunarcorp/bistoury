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

package qunar.tc.bistoury.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

/**
 * @author zhenyu.nie created on 2018 2018/9/21 17:05
 */
public class Snapshot {

    private enum Status {
        notInit, inited, fail
    }

    @JsonIgnore
    private volatile Status init = Status.notInit;

    private final String id;

    private final String source;

    private final int line;

    private Map<String, String> staticFields;

    private Map<String, String> fields;

    private Map<String, String> localVariables;

    private String stacktrace;

    private volatile long expireTime;

    public Snapshot(String id, String source, int line, long expireTime) {
        this.id = id;
        this.source = source;
        this.line = line;
        this.expireTime = expireTime;
    }

    @JsonIgnore
    public boolean isInit() {
        return init == Status.inited;
    }

    @JsonIgnore
    public boolean isFail() {
        return init == Status.fail;
    }

    public void markInited() {
        this.init = Status.inited;
    }

    public void markFail() {
        this.init = Status.fail;
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public int getLine() {
        return line;
    }

    public Map<String, String> getStaticFields() {
        return staticFields;
    }

    public void setStaticFields(Map<String, String> staticFields) {
        this.staticFields = staticFields;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public Map<String, String> getLocalVariables() {
        return localVariables;
    }

    public void setLocalVariables(Map<String, String> localVariables) {
        this.localVariables = localVariables;
    }

    public String getStacktrace() {
        return stacktrace;
    }

    public void setStacktrace(String stacktrace) {
        this.stacktrace = stacktrace;
    }


    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public void refreshExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    @Override
    public String toString() {
        return "Snapshot{" +
                "init=" + init +
                ", id='" + id + '\'' +
                ", source='" + source + '\'' +
                ", line=" + line +
                ", staticFields=" + staticFields +
                ", fields=" + fields +
                ", localVariables=" + localVariables +
                ", stacktrace='" + stacktrace + '\'' +
                ", expireTime=" + expireTime +
                '}';
    }
}
