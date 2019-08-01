package qunar.tc.bistoury.ui.service.impl;

import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.serverside.support.Application;
import qunar.tc.bistoury.ui.dao.ApplicationDao;
import qunar.tc.bistoury.ui.dao.ApplicationUserDao;
import qunar.tc.bistoury.ui.service.AppService;

import java.util.List;
import java.util.Set;

/**
 * @author zhenyu.nie created on 2018 2018/10/31 13:56
 */
@Service
public class AppServiceImpl implements AppService {

    @Autowired
    private ApplicationDao applicationDao;

    @Autowired
    private ApplicationUserDao applicationUserDao;

    @Override
    public Set<String> getApps(String userCode) {
        List<String> appCodes = this.applicationUserDao.getAppCodesByUserCode(userCode);
        return Sets.newHashSet(appCodes);
    }

    @Override
    public Application getAppInfo(String appCode) {
        return this.applicationDao.getApplicationByAppCode(appCode);
    }

}
