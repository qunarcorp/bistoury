package qunar.tc.bistoury.ui.service;

import java.util.List;

/**
 * @author cai.wen
 * @date 19-4-19
 */
public interface AdminAppService {

    List<String> searchApps(String searchKey, int size);

    boolean isAdminUser(String userName);
}
