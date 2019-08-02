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

/**
 * @author zhenyu.nie created on 2018 2018/11/28 19:49
 */
public class TypeResponse<T> {

    private String type;

    private CodeProcessResponse<T> data;

    public TypeResponse() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CodeProcessResponse<T> getData() {
        return data;
    }

    public void setData(CodeProcessResponse<T> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TypeResponse{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
