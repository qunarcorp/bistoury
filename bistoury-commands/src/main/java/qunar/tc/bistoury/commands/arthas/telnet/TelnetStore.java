package qunar.tc.bistoury.commands.arthas.telnet;

/**
 * @author zhenyu.nie created on 2018 2018/10/15 19:00
 */
public interface TelnetStore {

    Telnet getTelnet(int pid) throws Exception;

    Telnet tryGetTelnet() throws Exception;
}
