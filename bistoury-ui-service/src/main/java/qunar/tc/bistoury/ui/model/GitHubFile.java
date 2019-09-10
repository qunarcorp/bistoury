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

package qunar.tc.bistoury.ui.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author leix.xie
 * @date 2019/9/6 16:06
 * @describe
 */
public class GitHubFile {
    private final String fileName;
    private final String filePath;
    private final int size;
    private final String encoding;
    private final String content;
    private final String ref;

    @JsonCreator
    public GitHubFile(@JsonProperty("name") final String fileName,
                      @JsonProperty("path") final String filePath,
                      @JsonProperty("size") final int size,
                      @JsonProperty("encoding") final String encoding,
                      @JsonProperty("content") final String content,
                      @JsonProperty("sha") final String ref) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.size = size;
        this.encoding = encoding;
        this.content = content;
        this.ref = ref;
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
}
