package qunar.tc.bistoury.clientside.common.meta;

import java.util.Date;
import java.util.Map;

/**
 * @author zhenyu.nie created on 2019 2019/1/10 15:44
 */
public interface MetaStore {

    void update(Map<String, String> attrs);

    Map<String, String> getAgentInfo();

    String getStringProperty(String name);

    String getStringProperty(String name,String def);

    boolean getBooleanProperty(String name);

    boolean getBooleanProperty(String name, boolean def);

    Date getDateProperty(String name);

    int getIntProperty(String name);

    int getIntProperty(String name, int def);

    long getLongProperty(String name);

    long getLongProperty(String name, long def);

    float getFloatProperty(String name);

    float getFloatProperty(String name, float def);

    double getDoubleProperty(String name);

    double getDoubleProperty(String name, double def);
}
