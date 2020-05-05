package qunar.tc.bistoury.ui.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.ui.dao.ProfilerDao;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * @author cai.wen created on 2019/10/30 14:54
 */
@Repository
public class ProfilerDaoImpl implements ProfilerDao {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String SELECT_PROFILER_BY_PROFILER_ID_SQL = "SELECT id,profiler_id, operator, app_code, agent_id, pid, start_time, duration, interval_ms, mode, state, update_time" +
            " from bistoury_profiler where profiler_id=?";

    private static final String SELECT_LAST_RECORDS = "SELECT id,profiler_id, operator, app_code, agent_id, pid, start_time, duration, interval_ms, mode, state, update_time " +
            "FROM bistoury_profiler " +
            "where  app_code=? and agent_id=? and start_time>? order by start_time desc";

    private static final String SELECT_LAST_RECORD = "SELECT id,profiler_id, operator, app_code, agent_id, pid, start_time, duration, interval_ms, mode, state, update_time " +
            "FROM bistoury_profiler where app_code=? and agent_id=? " +
            "order by start_time desc limit 1";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init(){

    }

    @Override
    public List<Profiler> getRecords(String app, String agentId, LocalDateTime localTime) {
        return jdbcTemplate.query(SELECT_LAST_RECORDS, PROFILER_ROW_MAPPER, app, agentId, TIME_FORMATTER.format(localTime));
    }

    @Override
    public Profiler getRecordByProfilerId(String profilerId) {
        return jdbcTemplate.query(SELECT_PROFILER_BY_PROFILER_ID_SQL, PROFILER_RESULT_SET_EXTRACTOR, profilerId);
    }

    @Override
    public Optional<Profiler> getLastRecord(String app, String agentId) {
        List<Profiler> profilers = jdbcTemplate.query(SELECT_LAST_RECORD, PROFILER_ROW_MAPPER, app, agentId);
        return profilers.isEmpty() ? Optional.empty() : Optional.of(profilers.get(0));
    }

    private final ResultSetExtractor<Profiler> PROFILER_RESULT_SET_EXTRACTOR = resultSet -> {
        if (resultSet.next()) {
            return getProfiler(resultSet);
        }
        return null;
    };

    private final RowMapper<Profiler> PROFILER_ROW_MAPPER = (rs, rowNum) -> getProfiler(rs);

    private Profiler getProfiler(ResultSet rs) throws SQLException {
        Profiler profiler = new Profiler();
        profiler.setProfilerId(rs.getString("profiler_id"));
        profiler.setOperator(rs.getString("operator"));
        profiler.setAppCode(rs.getString("app_code"));
        profiler.setAgentId(rs.getString("agent_id"));
        profiler.setPid(rs.getInt("pid"));
        profiler.setId(rs.getInt("id"));
        profiler.setDuration(rs.getInt("duration"));
        profiler.setInterval(rs.getInt("interval_ms"));
        profiler.setMode(Profiler.Mode.fromCode(rs.getInt("mode")));
        profiler.setStartTime(rs.getTimestamp("start_time"));
        profiler.setUpdateTime(rs.getTimestamp("update_time"));
        profiler.setState(Profiler.State.fromCode(rs.getInt("state")));
        return profiler;
    }
}