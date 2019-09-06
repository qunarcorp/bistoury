package qunar.tc.bistoury.serverside.common.registry;

import java.util.List;

/**
 * @author cai.wen created on 2019/9/3 18:04
 */
public interface RegistryClient {

    void deleteNode(String node) throws Exception;

    void addNode(String node) throws Exception;

    List<String> getChildren() throws Exception;

    void close() throws Exception;
}
