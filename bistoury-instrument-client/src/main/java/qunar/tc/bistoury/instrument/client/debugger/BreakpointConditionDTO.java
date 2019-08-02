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

package qunar.tc.bistoury.instrument.client.debugger;

import java.util.Map;

/**
 * 判断断点条件是否为真时使用的rootObject
 * 持有运行时类的上下文。同时也变相规定了程序所接受的断点条件的编写规范。
 */
class BreakpointConditionDTO {
    private Map<String, Object> localVariables;
    private Map<String, Object> fields;
    private Map<String, Object> staticFields;

    public Map<String, Object> getLocalVariables() {
        return localVariables;
    }

    public void setLocalVariables(Map<String, Object> localVariables) {
        this.localVariables = localVariables;
    }

    public Map<String, Object> getFields() {
        return fields;
    }

    public void setFields(Map<String, Object> fields) {
        this.fields = fields;
    }

    public Map<String, Object> getStaticFields() {
        return staticFields;
    }

    public void setStaticFields(Map<String, Object> staticFields) {
        this.staticFields = staticFields;
    }

    @Override
    public String toString() {
        return "BreakpointConditionDTO{" +
                "localVariables=" + localVariables +
                ", fields=" + fields +
                ", staticFields=" + staticFields +
                '}';
    }
}
