package qunar.tc.bistoury.commands;

import java.text.MessageFormat;

/**
 * @author leix.xie
 * @date 2019/7/10 16:26
 * @describe
 */
public class StringFormatTest {
    public static void main(String[] args) {
        String str = "http://www.example.com/nexus/content/groups/public/{0}/{1}/{2}/{1}-{2}-sources.jar";
        String groupId = "qunar/tc";
        String artifactId = "qmq-client";
        String version = "4.1.7";
        System.out.println(MessageFormat.format(str, groupId, artifactId, version));
    }
}