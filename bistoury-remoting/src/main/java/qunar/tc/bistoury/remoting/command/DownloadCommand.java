package qunar.tc.bistoury.remoting.command;

/**
 * @author leix.xie
 * @date 2019/11/5 15:22
 * @describe
 */
public class DownloadCommand {
    private String path;
    private String dir;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    @Override
    public String toString() {
        return "DownloadCommand{" +
                "path='" + path + '\'' +
                ", dir='" + dir + '\'' +
                '}';
    }
}
