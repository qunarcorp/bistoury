package qunar.tc.bistoury.agent.common.kv;

/**
 * @author zhenyu.nie created on 2019 2019/1/8 17:20
 */
public interface KvDb {

    String get(String key);

    void put(String key, String value);
}
