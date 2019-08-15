package qunar.tc.bistoury.application.mysql.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.application.api.ApplicationService;
import qunar.tc.bistoury.application.api.pojo.Application;
import qunar.tc.bistoury.application.api.pojo.PermissionDenyException;
import qunar.tc.bistoury.application.mysql.dao.ApplicationDao;
import qunar.tc.bistoury.application.mysql.dao.ApplicationUserDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author xkrivzooh
 * @since 2019/8/14
 */
@Service
public class ApplicationServiceImpl implements ApplicationService {

	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private ApplicationUserDao applicationUserDao;

	private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceImpl.class);

	@Override
	public List<Application> getAllApplications() {
		return applicationDao.getAllApplications();
	}

	@Override
	public List<Application> getAllApplications(String userCode) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(userCode), "user code cannot be null or empty");
		List<String> appCodes = this.applicationUserDao.getAppCodesByUserCode(userCode);
		List<Application> applications = this.applicationDao.getApplicationsByAppCodes(appCodes);
		return applications;
	}

	@Override
	public List<String> getAppOwner(String appCode) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(appCode), "app code cannot be null or empty");
		return this.applicationUserDao.getUsersByAppCode(appCode);
	}

	@Override
	@Transactional
	public int save(Application application, String loginUser, boolean admin) {
		String appCode = application.getCode();
		Preconditions.checkArgument(!Strings.isNullOrEmpty(appCode), "app code cannot be null or empty");
		Preconditions.checkArgument(!CollectionUtils.isEmpty(application.getOwner()), "owner cannot be null or empty");
		Application oldApplication = this.applicationDao.getApplicationByAppCode(appCode);

		if (application.getId() <= 0) {
			if (oldApplication != null) {
				throw new RuntimeException("应用新增失败，应用" + appCode + "已存在");
			}
			application.setCreator(loginUser);
			application.setCreateTime(new Date());
			List<String> owner = application.getOwner();
			if (!owner.contains(loginUser)) {
				owner.add(loginUser);
			}
			this.applicationUserDao.batchAddAppUser(owner, appCode);
			logger.info("{} add application {}", loginUser, application);
			return this.applicationDao.createApplication(application);
		} else {
			if (oldApplication == null) {
				throw new RuntimeException("数据错误");
			}
			verification(oldApplication, loginUser, admin);

			//去除与新owner的交集后剩余的需要删除
			List<String> oldOwners = this.getAppOwner(appCode);
			ArrayList<String> oldOwnersCopy = Lists.newArrayList(oldOwners);
			//去除与老的owner的交集后需要新增
			List<String> newOwners = application.getOwner();

			logger.info("{} update application {}, owner {} to {}", loginUser, application, oldOwners, newOwners);

			oldOwners.removeAll(newOwners);
			newOwners.removeAll(oldOwnersCopy);
			for (String owner : oldOwners) {
				this.applicationUserDao.removeAppUser(owner, appCode);
			}
			this.applicationUserDao.batchAddAppUser(newOwners, appCode);
			return this.applicationDao.updateApplication(application);
		}
	}

	private boolean verification(Application application, String loginUser, boolean admin) {
		List<String> owner = this.getAppOwner(application.getCode());
		if (owner.contains(loginUser) || admin) {
			return true;
		} else {
			throw new PermissionDenyException("仅应用负责人可修改应用信息");
		}
	}
}
