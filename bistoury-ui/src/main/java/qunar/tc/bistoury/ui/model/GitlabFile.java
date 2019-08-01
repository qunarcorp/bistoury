package qunar.tc.bistoury.ui.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author keli.wang
 */
public class GitlabFile {
    private final String fileName;
    private final String filePath;
    private final int size;
    private final String encoding;
    private final String content;
    private final String ref;
    private final String blobId;
    private final String commitId;

    @JsonCreator
    public GitlabFile(@JsonProperty("file_name") final String fileName,
                      @JsonProperty("file_path") final String filePath,
                      @JsonProperty("size") final int size,
                      @JsonProperty("encoding") final String encoding,
                      @JsonProperty("content") final String content,
                      @JsonProperty("ref") final String ref,
                      @JsonProperty("blob_id") final String blobId,
                      @JsonProperty("commit_id") final String commitId) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.size = size;
        this.encoding = encoding;
        this.content = content;
        this.ref = ref;
        this.blobId = blobId;
        this.commitId = commitId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public int getSize() {
        return size;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getContent() {
        return content;
    }

    public String getRef() {
        return ref;
    }

    public String getBlobId() {
        return blobId;
    }

    public String getCommitId() {
        return commitId;
    }
}
