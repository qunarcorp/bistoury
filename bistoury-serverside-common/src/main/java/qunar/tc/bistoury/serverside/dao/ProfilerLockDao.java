package qunar.tc.bistoury.serverside.dao;

public interface ProfilerLockDao {

    void insert(String appCode, String agentId);

    void delete(String appCode, String agentId);
}
