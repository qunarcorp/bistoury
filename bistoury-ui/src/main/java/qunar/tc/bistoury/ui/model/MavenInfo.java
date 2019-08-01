package qunar.tc.bistoury.ui.model;

import java.util.Objects;

/**
 * @author: leix.xie
 * @date: 2019/4/3 10:58
 * @describe：
 */
public class MavenInfo {
    private String artifactId;
    private String groupId;
    private String version;

    public MavenInfo(String artifactId, String groupId, String version) {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MavenInfo mavenInfo = (MavenInfo) o;
        return Objects.equals(artifactId, mavenInfo.artifactId) &&
                Objects.equals(groupId, mavenInfo.groupId) &&
                Objects.equals(version, mavenInfo.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(artifactId, groupId, version);
    }

    @Override
    public String toString() {
        return "MavenInfo{" +
                "artifactId='" + artifactId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
