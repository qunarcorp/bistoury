package qunar.tc.bistoury.proxy.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class ProfilerLockDaoImpl implements ProfilerLockDao {

    private static final String LOCK_PROFILER_SQL = "insert into bistoury_profiler_lock (app_code, agent_id) VALUES (?,?)";

    private static final String UNLOCK_PROFILER_SQL = "delete from bistoury_profiler_lock where app_code=? and agent_id=?";

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void insert(String appCode, String agentId) {
        jdbcTemplate.update(LOCK_PROFILER_SQL, appCode, agentId);
    }

    @Override
    public void delete(String appCode, String agentId) {
        jdbcTemplate.update(UNLOCK_PROFILER_SQL, appCode, agentId);
    }
}
