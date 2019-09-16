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

import com.google.common.base.Charsets;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Request;
import com.ning.http.client.Response;
import org.junit.Test;
import qunar.tc.bistoury.common.AsyncHttpClientHolder;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author leix.xie
 * @date 2019/9/5 14:17
 * @describe
 */
public class GithubTest {
    private static final String token = "token 9db2ef44286ba5e2f37e8441a20c55f2e7cf8fd2";
    private AsyncHttpClient client = AsyncHttpClientHolder.getInstance();

    @Test
    public void githubTest() throws IOException, ExecutionException, InterruptedException {
        Request request = client.prepareGet("https://api.github.com/repos/xleiy/algorithm/git/trees/master")
                .addHeader("Accept", "application/json")
                .addHeader("'content-type", "application/json")
                .addHeader("Authorization", token)
                .build();
        ListenableFuture<Response> future = client.executeRequest(request);
        Response response = future.get();
        System.out.println(response.getStatusCode());
        System.out.println(response.getResponseBody(Charsets.UTF_8.name()));
    }

    @Test
    public void githubFileTest() throws ExecutionException, InterruptedException, IOException {
        Request request = client.prepareGet("https://api.github.com/repos/xleiy/algorithm/contents/src/main/java/com/xx/leetcode/AddTwoNumbers.java")
                .addQueryParam("ref", "master")
                .addHeader("Accept", "application/json")
                .addHeader("'content-type", "application/json")
                .addHeader("Authorization", token)
                .build();
        ListenableFuture<Response> future = client.executeRequest(request);
        Response response = future.get();
        System.out.println(response.getStatusCode());
        System.out.println(response.getResponseBody(Charsets.UTF_8.name()));
    }
}
