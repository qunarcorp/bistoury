package qunar.tc.bistoury.serverside.common.registry;

import java.util.List;

/**
 * @author cai.wen created on 2019/9/2 15:43
 */
public interface RegistryService {

    void online();

    void offline();

    List<String> getAllProxyUrls();
}
