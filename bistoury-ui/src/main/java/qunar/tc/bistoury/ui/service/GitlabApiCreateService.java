package qunar.tc.bistoury.ui.service;

import org.gitlab.api.GitlabAPI;

/**
 * @author keli.wang
 */
public interface GitlabApiCreateService {
    GitlabAPI create();

    GitlabAPI createForUser(final String userCode);
}
