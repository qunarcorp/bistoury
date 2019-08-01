package qunar.tc.bistory;

import org.junit.Test;
import qunar.tc.bistoury.attach.file.FileOperateFactory;
import qunar.tc.bistoury.attach.file.bean.FileBean;
import qunar.tc.bistoury.attach.file.impl.DefaultFileServiceImpl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author leix.xie
 * @date 2019-07-25 18:34
 * @describe
 */
public class JarJarFileRead {


    @Test
    public void replaceJarWithUnPackDir() throws Exception {
        String path = "jar:file:/Users/leix.xie/Downloads/demo-0.0.1/demo-0.0.1-SNAPSHOT.jar!/BOOT-INF/classes!/com/example/demo/DemoApplication.class";
        path = FileOperateFactory.replaceJarWithUnPackDir(path);
        System.out.println(path);
    }

    @Test
    public void readJarJarFileTest() throws IOException, URISyntaxException {
        System.setProperty("bistoury.store.path", "~/workspace/q/bistoury/store");
        String path = "file:/Users/leix.xie/Downloads/demo-0.0.1/demo-0.0.1-SNAPSHOT.jar!/BOOT-INF/lib/spring-boot-2.1.6.RELEASE.jar!/pmo.jar";
        //path = "/Users/leix.xie/Desktop/releaseInfo.properties";
        String file = FileOperateFactory.getFile(path);
        System.out.println(file);
    }

    @Test
    public void jarFileTest() throws IOException {
        List<FileBean> fileBeans = FileOperateFactory.listFiles("file:/Users/leix.xie/Downloads/demo-0.0.1/demo-0.0.1-SNAPSHOT.jar!/BOOT-INF");
        //List<FileBean> fileBeans = FileOperateFactory.listFiles("file:/Users/leix.xie/Downloads/demo-0.0.1/demo-0.0.1-SNAPSHOT.jar!/BOOT-INF/lib");
        for (FileBean fileBean : fileBeans) {
            System.out.println(fileBean);
        }
    }

    @Test
    public void defaultFileTest() throws MalformedURLException {
        DefaultFileServiceImpl fileService = new DefaultFileServiceImpl();
        Path path = Paths.get("/Users/leix.xie/test");
        List<FileBean> fileBeans = fileService.listFiles(path.toUri().toURL());
        for (FileBean fileBean : fileBeans) {
            System.out.println(fileBean);
        }
    }
}
