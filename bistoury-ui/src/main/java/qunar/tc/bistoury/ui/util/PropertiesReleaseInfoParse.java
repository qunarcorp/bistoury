package qunar.tc.bistoury.ui.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.Throwables;
import qunar.tc.bistoury.ui.model.ReleaseInfo;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

/**
 * @author leix.xie
 * @date 2019/7/10 10:45
 * @describe
 */
public class PropertiesReleaseInfoParse implements ReleaseInfoParse {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesReleaseInfoParse.class);

    private static final String PROJECT_KEY = "project";
    private static final String MODULE_KEY = "module";
    private static final String OUTPUT_KEY = "output";

    @Override
    public ReleaseInfo parseReleaseInfo(String content) {

        Properties properties = new Properties();
        try {
            properties.load(new StringReader(content));
        } catch (IOException e) {
            logger.error("load properties error, content: {}", content, e);
            throw Throwables.propagate(e);
        }

        String project = properties.getProperty(PROJECT_KEY);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(project), "project cannot be null or empty");

        String module = properties.getProperty(MODULE_KEY);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(module), "module cannot be null or empty");

        String output = properties.getProperty(OUTPUT_KEY);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(output), "output cannot be null or empty");
        return new ReleaseInfo(project, module, output);
    }
}
