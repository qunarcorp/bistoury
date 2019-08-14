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

package qunar.tc.bistoury.application.api.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author leix.xie
 * @date 2019/7/2 10:48
 * @describe
 */
public class Application implements Serializable {
    private int id;
    private String code;
    private String name;
    private String groupCode;
    private List<String> owner;
    /**
     * UNAUDIT(0, "未审核"),
     * PASS(1, "审核通过"),
     * REJECT(2, "申请被拒绝"),
     * DISCARD(3, "已废弃");
     */
    private int status;
    private String creator;
    private Date createTime;

    public Application() {
    }

    public Application(String code) {
        this.code = code;
    }

    public Application(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Application(String code, String name, String groupCode, List<String> owner, int status, String creator, Date createTime) {
        this.code = code;
        this.name = name;
        this.groupCode = groupCode;
        this.owner = owner;
        this.status = status;
        this.creator = creator;
        this.createTime = createTime;
    }

    public Application(String code, String name, String groupCode, int status, String creator) {
        this.code = code;
        this.name = name;
        this.groupCode = groupCode;
        this.status = status;
        this.creator = creator;
    }

    public Application(String code, String name, String groupCode, int status, String creator, Date createTime) {
        this.code = code;
        this.name = name;
        this.groupCode = groupCode;
        this.status = status;
        this.creator = creator;
        this.createTime = createTime;
    }

    public Application(int id, String code, String name, String groupCode, int status, String creator, Date createTime) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.groupCode = groupCode;
        this.status = status;
        this.creator = creator;
        this.createTime = createTime;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupCode() {
        return this.groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public List<String> getOwner() {
        return this.owner;
    }

    public void setOwner(List<String> owner) {
        this.owner = owner;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
