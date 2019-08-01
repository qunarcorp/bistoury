package qunar.tc.bistoury.remoting.protocol;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import qunar.tc.bistoury.remoting.command.*;

import java.util.Map;
import java.util.Set;

import static qunar.tc.bistoury.remoting.protocol.CommandCode.*;

/**
 * @author zhenyu.nie created on 2019 2019/5/23 20:04
 */
public class DefaultCodeTypeMappingStore implements CodeTypeMappingStore {

    private final Map<Class<?>, Set<Integer>> typeCodeMappings = Maps.newHashMap();

    private final Map<Integer, Class<?>> codeTypeMappings = Maps.newHashMap();

    DefaultCodeTypeMappingStore() {
        final Class stringClass = String.class;

        //arthas
        register(String.class, ImmutableSet.of(REQ_TYPE_ARTHAS.getCode(),
                REQ_TYPE_DEBUG.getCode(),
                REQ_TYPE_MONITOR.getCode(),
                REQ_TYPE_JAR_INFO.getCode(),
                REQ_TYPE_CONFIG.getCode(),
                REQ_TYPE_JAR_DEBUG.getCode()));

        //jstack
        register(CpuTimeCommand.class, ImmutableSet.of(REQ_TYPE_CPU_JSTACK_TIMES.getCode()));
        register(String.class, ImmutableSet.of(REQ_TYPE_CPU_JSTACK_THREADS.getCode()));
        register(ThreadNumCommand.class, ImmutableSet.of(REQ_TYPE_CPU_THREAD_NUM.getCode()));
        //decompiler
        register(DecompilerCommand.class, ImmutableSet.of(REQ_TYPE_DECOMPILER.getCode()));
        //HeapHisto
        register(HeapHistoCommand.class, ImmutableSet.of(REQ_TYPE_HOST_HEAP_HISTO.getCode()));
        //host
        register(Integer.class, ImmutableSet.of(REQ_TYPE_HOST_JVM.getCode()));
        //thread info
        register(ThreadCommand.class, ImmutableSet.of(REQ_TYPE_HOST_THREAD.getCode()));
        //monitor query
        register(MonitorCommand.class, ImmutableSet.of(REQ_TYPE_QMONITOR_QUERY.getCode()));
        //heartbeat
        register(stringClass, ImmutableSet.of(ResponseCode.RESP_TYPE_HEARTBEAT.getCode()));
        //linux command process
        register(MachineCommand.class, ImmutableSet.of(CommandCode.REQ_TYPE_COMMAND.getCode()));
        //java command process
        register(MachineCommand.class, ImmutableSet.of(REQ_TYPE_JAVA.getCode()));
        //QJTools processor
        register(MachineCommand.class, ImmutableSet.of(CommandCode.REQ_TYPE_QJTOOLS.getCode()));
        //meta refresh
        register(stringClass, ImmutableSet.of(REQ_TYPE_REFRESH_AGENT_INFO.getCode()));
        //meta refresh tip
        register(stringClass, ImmutableSet.of(REQ_TYPE_REFRESH_TIP.getCode()));
        //cancel
        register(stringClass, ImmutableSet.of(REQ_TYPE_CANCEL.getCode()));

    }

    public void register(Class<?> type, Set<Integer> codes) {
        for (Integer code : codes) {
            codeTypeMappings.put(code, type);
        }
    }

    @Override
    public Class<?> getMappingType(int code) {
        return codeTypeMappings.get(code);
    }
}
