package qunar.tc.bistoury.serverside.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import qunar.tc.bistoury.serverside.bean.Profiler;
import qunar.tc.bistoury.serverside.jdbc.JdbcTemplateHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author cai.wen created on 2019/10/30 14:54
 */
public class ProfilerDaoImpl implements ProfilerDao {

    private static final String PREPARE_PROFILER_SQL = "insert into bistoury_profiler " +
            "(profiler_id, operator, app_code, agent_id, pid, start_time, duration,frequency,state) " +
            "select ?, ?, ?, ?, ?, ?, ?, ?, 2 " +
            "where not exists(select NULL from bistoury_profiler where agent_id = ? and pid = ? and (state = 0 or state = 2))";

    private static final String UPDATE_PROFILER_STATE_SQL = "update bistoury_profiler set state=? where profiler_id=?";

    private static final String SELECT_PROFILER_BY_STATE_SQL = "select * from bistoury_profiler where state=? and start_time>DATE_SUB(CURDATE(), INTERVAL ? hour ) ";

    private static final String SELECT_PROFILER_BY_PROFILER_ID_SQL = "select * from bistoury_profiler where profiler_id=?";

    private static final String SELECT_LAST_THREE_DAY_RECORD = "SELECT * FROM bistoury_profiler " +
            "where  app_code=? and agent_id=? and start_time>DATE_SUB(CURDATE(), INTERVAL ? hour ) " +
            "order by start_time desc";

    private JdbcTemplate jdbcTemplate = JdbcTemplateHolder.getOrCreateJdbcTemplate();

    @Override
    public List<Profiler> getRecords(String app, String agentId, int hours) {
        return jdbcTemplate.query(SELECT_LAST_THREE_DAY_RECORD, PROFILER_ROW_MAPPER, app, agentId, hours);
    }

    @Override
    public Profiler getRecordById(String profilerId) {
        return jdbcTemplate.query(SELECT_PROFILER_BY_PROFILER_ID_SQL, PROFILER_RESULT_SET_EXTRACTOR, profilerId);
    }

    @Override
    public void changeState(Profiler.State state, String profilerId) {
        jdbcTemplate.update(UPDATE_PROFILER_STATE_SQL, state.code, profilerId);
    }

    @Override
    public void prepareProfiler(Profiler profiler) {
        int insertState = jdbcTemplate.update(PREPARE_PROFILER_SQL,
                profiler.getProfilerId(), profiler.getOperator(), profiler.getAppCode(), profiler.getAgentId(), profiler.getPid(), new Date(), profiler.getDuration(), profiler.getFrequency(),
                profiler.getAgentId(), profiler.getPid()
        );
        if (insertState == 0) {
            throw new IllegalStateException("insert new profiler record error.");
        }
    }

    @Override
    public List<Profiler> getRecordsByState(Profiler.State state, int hours) {
        return jdbcTemplate.query(SELECT_PROFILER_BY_STATE_SQL, PROFILER_ROW_MAPPER, state.code, hours);
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
        profiler.setFrequency(rs.getInt("frequency"));
        profiler.setStartTime(rs.getTimestamp("start_time"));
        profiler.setUpdateTime(rs.getTimestamp("update_time"));
        profiler.setState(Profiler.State.fromCode(rs.getInt("state")));
        return profiler;
    }
}