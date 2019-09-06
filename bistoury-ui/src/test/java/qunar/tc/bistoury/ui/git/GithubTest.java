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

package qunar.tc.bistoury.ui.git;

import org.junit.Test;
import org.kohsuke.github.GHEventInfo;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.List;

/**
 * @author leix.xie
 * @date 2019/9/5 14:17
 * @describe
 */
public class GithubTest {
    @Test
    public void githubTest() throws IOException {
        GitHub gitHub = GitHub.connectUsingOAuth("4647e0311fe1fa670877855a2c56a14bd092c22f");
        //GitHub gitHub = GitHub.connect();
        String apiUrl = gitHub.getApiUrl();
        System.out.println(apiUrl);

        List<GHEventInfo> events = gitHub.getEvents();
        for (GHEventInfo event : events) {
            System.out.println(event.getActor().getName() + "\t" + event.getRepository().getFullName());
        }
    }
}
