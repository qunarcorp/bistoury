package qunar.tc.bistoury.ui.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.NamedThreadFactory;
import qunar.tc.bistoury.serverside.support.Application;
import qunar.tc.bistoury.ui.dao.ApplicationDao;
import qunar.tc.bistoury.ui.service.AdminAppService;
import qunar.tc.bistoury.ui.service.UserService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author cai.wen
 * @date 19-4-19
 */
@Service
public class AdminAppServiceImpl implements AdminAppService {

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE =
            Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("refresh-all-app"));

    private volatile ImmutableSet<String> adminApps = ImmutableSet.of();

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() {
        refreshAllApp();

        SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(() -> refreshAllApp(), 10, 10, TimeUnit.MINUTES);
    }

    private void refreshAllApp() {
        List<Application> allApplications = applicationDao.getAllApplications();
        adminApps = ImmutableSet.copyOf(Lists.transform(allApplications, Application::getCode));
    }

    @Override
    public List<String> searchApps(String keyInput, int size) {
        final String key = Strings.nullToEmpty(keyInput).toLowerCase();
        ImmutableSet<String> adminApps = this.adminApps;
        if (Strings.isNullOrEmpty(key)) {
            return adminApps.asList().subList(0, size);
        }

        int needAddSize = size;
        List<String> matchApps = new ArrayList<>(size);
        if (adminApps.contains(key)) {
            matchApps.add(key);
            needAddSize--;
        }

        adminApps.stream()
                .filter((app) -> app.contains(key) && !app.equals(key))
                .limit(needAddSize)
                .forEach(matchApps::add);
        return matchApps;
    }

    @Override
    public boolean isAdminUser(String userName) {
        return this.userService.isAdmin(userName);
    }
}
