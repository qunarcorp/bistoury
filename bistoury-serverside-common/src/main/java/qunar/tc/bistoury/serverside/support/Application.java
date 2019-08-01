package qunar.tc.bistoury.serverside.support;

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
