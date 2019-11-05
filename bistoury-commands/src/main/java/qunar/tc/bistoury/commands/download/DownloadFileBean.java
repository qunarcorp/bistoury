package qunar.tc.bistoury.commands.download;

/**
 * @author leix.xie
 * @date 2019/11/4 16:43
 * @describe
 */
public class DownloadFileBean {
    private String name;
    private String path;
    private String code;
    private long size;
    private long modifiedTime;

    public DownloadFileBean(String name, String path, String code, long size, long modifiedTime) {
        this.name = name;
        this.path = path;
        this.code = code;
        this.size = size;
        this.modifiedTime = modifiedTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
