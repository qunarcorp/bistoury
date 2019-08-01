package qunar.tc.decompiler;


import com.google.common.base.Strings;
import qunar.tc.decompiler.main.decompiler.ConsoleDecompiler;
import qunar.tc.decompiler.main.decompiler.PrintStreamLogger;
import qunar.tc.decompiler.main.extern.IFernflowerPreferences;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author: leix.xie
 * @date: 2019/2/21 10:46
 * @describe：
 */
public class ClassTest {
    private static final String SEPARATOR = System.lineSeparator();
    private static final String NOTES;
    private static final String NOTE_CHAR = "// ";

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(NOTE_CHAR).append(SEPARATOR);
        sb.append(NOTE_CHAR).append("Source code recreated from a .class file by QUNAR Agent").append(SEPARATOR);
        sb.append(NOTE_CHAR).append("(powered by Fernflower decompiler)").append(SEPARATOR);
        sb.append(NOTE_CHAR).append(SEPARATOR);
        sb.append(SEPARATOR);
        NOTES = sb.toString();
    }

    /**
     * 主方法
     */
    public static void main(String[] args) throws ClassNotFoundException, URISyntaxException, IOException {
        Class<?> clazz = Class.forName("com.google.common.base.Strings");
        System.out.println(getPath(clazz));

    }

    private static ClassInfo getPath(Class clazz) throws IOException {
        final String classPath = clazz.getName().replace('.', '/') + ".class";
        URL url;
        CodeSource source = clazz.getProtectionDomain().getCodeSource();
        if (source != null) {
            url = source.getLocation();
            if (url != null) {
                String ur = url.toString();
                ClassInfo classInfo = generateClassInfo(url, clazz);
                if (ur.endsWith(".jar")) {
                    classInfo.setClassPath(ur + "!" + File.separator + classPath);
                } else {
                    classInfo.setClassPath(ur + classPath);
                }
                return classInfo;
            }
        }
        url = ClassLoader.getSystemResource(classPath);
        if (url != null) {
            return generateClassInfo(url, clazz);
        }
        url = clazz.getClassLoader().getResource(classPath);
        if (url != null) {
            return generateClassInfo(url, clazz);
        }
        return null;
    }

    private static ClassInfo generateClassInfo(URL url, Class clazz) throws IOException {
        ClassInfo classInfo = new ClassInfo();
        classInfo.setClassPath(url.toString());

        if (isJarFile(url)) {
            CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
            final String path;
            if (codeSource == null || Strings.isNullOrEmpty(path = codeSource.getLocation().getPath())) {
                classInfo.setMaven(false);
                return classInfo;
            }
            JarFile jarFile = new JarFile(path);
            classInfo.setJarName(jarFile.getName());
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().endsWith("/pom.properties")) {
                    Properties properties = readJarFile(path, jarEntry.getName());
                    classInfo.setMavenInfo(properties);
                    classInfo.setMaven(true);
                    break;
                }
            }
        }
        return classInfo;
    }

    private static Properties readJarFile(final String jarPath, final String filePath) throws IOException {
        URL url = new URL("jar:file:" + jarPath + "!/" + filePath);
        try (InputStream stream = url.openStream();) {
            Properties properties = new Properties();
            properties.load(stream);
            return properties;
        }
    }

    private static void decompile(final String path) throws IOException {
        System.out.println(path);
        Map<String, Object> options = new HashMap<>();
        options.putAll(IFernflowerPreferences.DEFAULTS);

        //反编译保留泛型
        options.put(IFernflowerPreferences.DECOMPILE_GENERIC_SIGNATURES, "1");
        //缩进
        options.put(IFernflowerPreferences.INDENT_STRING, "    ");
        options.put(IFernflowerPreferences.LOG_LEVEL, "warn");

        //代码末尾的行号
        options.put(IFernflowerPreferences.BYTECODE_SOURCE_MAPPING, "1");
        options.put(IFernflowerPreferences.DUMP_ORIGINAL_LINES, "1");

        //文件下方信息
        options.put(IFernflowerPreferences.UNIT_TEST_MODE, "1");

        //反编译后上方的提示信息
        options.put(IFernflowerPreferences.BANNER, NOTES);

        File tempDir = new File("E:\\qunar\\tcdev\\bistoury\\bistoury-decompiler-fernflower\\target");
        File targetDir = new File(tempDir, "decompiled");
        targetDir.delete();
        targetDir.mkdirs();
        ConsoleDecompiler decompiler = new ConsoleDecompiler(targetDir, options, new PrintStreamLogger(System.out));
        File file = new File(path);
        decompiler.addStream(new URL(path).openStream(), "File.class", URLDecoder.decode(new URL(path).toString()));
        decompiler.decompileContext();
    }

    private static boolean isJarFile(URL resource) {
        return "jar".equalsIgnoreCase(resource.getProtocol()) || resource.getFile().endsWith(".jar");
    }

    private static File getFileFromJar(String path, final String fileName) {
        String[] paths = path.split("!");
        try {
            JarFile jarFile = new JarFile(paths[0]);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().equalsIgnoreCase(fileName)) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void print(byte[] bytes) {
        File file = new File("E:\\qunar\\tcdev\\bistoury\\bistoury-instrument-client\\target\\App.class");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClassInfo {
    private String classPath;
    private Boolean jar = false;
    private String jarName;
    private String jarPath;
    private Boolean maven = false;
    private Properties mavenInfo;

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public Boolean getJar() {
        return jar;
    }

    public void setJar(Boolean jar) {
        this.jar = jar;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public Boolean getMaven() {
        return maven;
    }

    public void setMaven(Boolean maven) {
        this.maven = maven;
    }

    public Properties getMavenInfo() {
        return mavenInfo;
    }

    public void setMavenInfo(Properties mavenInfo) {
        this.mavenInfo = mavenInfo;
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "classPath='" + classPath + '\'' +
                ", jar=" + jar +
                ", jarName='" + jarName + '\'' +
                ", jarPath='" + jarPath + '\'' +
                ", maven=" + maven +
                ", mavenInfo=" + mavenInfo +
                '}';
    }
}