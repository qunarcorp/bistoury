package qunar.tc.bistoury.proxy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.proxy.web.dao.AppServerDao;
import qunar.tc.bistoury.serverside.support.AppServer;

import java.util.List;

public class AppCenterServerFinder implements ServerFinder {

    private static final Logger logger = LoggerFactory.getLogger(AppCenterServerFinder.class);

    private final AppServerDao appServerDao;

    public AppCenterServerFinder(AppServerDao appServerDao) {
        this.appServerDao = appServerDao;
    }

    @Override
    public List<AppServer> findAgents(String app) {
        return appServerDao.getAppServerByAppCode(app);
    }
}
