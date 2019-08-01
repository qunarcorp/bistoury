package qunar.tc.bistoury.ui.model;

/**
 * @author leix.xie
 * @date 2019/7/10 10:20
 * @describe
 */
public class ReleaseInfo {
    /**
     * 发布项目名
     */
    private String project;
    /**
     * 应用所在module，没有时，module为一个英文句号[.]
     */
    private String module;
    /**
     * 发布的版本号/分支/tag
     */
    private String output;

    public ReleaseInfo() {
    }

    public ReleaseInfo(String project, String module, String output) {
        this.project = project;
        this.module = module;
        this.output = output;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    @Override
    public String toString() {
        return "ReleaseInfo{" +
                "project='" + project + '\'' +
                ", module='" + module + '\'' +
                ", output='" + output + '\'' +
                '}';
    }
}
