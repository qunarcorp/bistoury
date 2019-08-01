package qunar.tc.bistoury.commands.decompiler;

import qunar.tc.bistoury.common.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author: leix.xie
 * @date: 2019/3/1 14:28
 * @describe：
 */
public class DecompilerTest {
    /**
     * 主方法
     */
    public static void main(String[] args) throws ClassNotFoundException, IOException {
        String tmpdir = "E:\\workspace\\tcdev\\bistoury\\bistoury-commands\\target";
        File tempDir = new File(tmpdir);
        File targetDir = new File(tempDir, "decompiled");
        targetDir.delete();
        targetDir.mkdirs();
        Decompiler decompiler = new Decompiler(targetDir);


        Class<?> clazz = Class.forName("java.io.File");
        final String classPath = clazz.getName().replace('.', '/') + ".class";
        URL url = ClassLoader.getSystemResource(classPath);
        decompiler.addStream(url.openStream(), clazz.getSimpleName() + ".class", url.toString());
        decompiler.decompileContext();
        File file = new File(targetDir, clazz.getSimpleName() + ".java");
        String fileContent = FileUtil.readFile(file);
        System.out.println(fileContent);
        //file.delete();
    }
}