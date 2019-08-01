package qunar.tc.bistoury.attach.file.bean;

/**
 * @author: leix.xie
 * @date: 2019/2/13 17:51
 * @describe：
 */
public class FileBean {
    private String name;
    private long modifiedTime;
    private long size;

    public FileBean(String name, long modifiedTime, long size) {
        this.name = name;
        this.modifiedTime = modifiedTime;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "FileBean{" +
                "name='" + name + '\'' +
                ", modifiedTime=" + modifiedTime +
                ", size=" + size +
                '}';
    }
}
