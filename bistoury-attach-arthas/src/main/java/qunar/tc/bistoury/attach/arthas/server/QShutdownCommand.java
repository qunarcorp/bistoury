package qunar.tc.bistoury.attach.arthas.server;

import com.taobao.arthas.core.advisor.Enhancer;
import com.taobao.arthas.core.shell.ShellServer;
import com.taobao.arthas.core.shell.command.AnnotatedCommand;
import com.taobao.arthas.core.shell.command.CommandProcess;
import com.taobao.arthas.core.util.affect.EnhancerAffect;
import com.taobao.arthas.core.util.matcher.WildcardMatcher;
import com.taobao.middleware.cli.annotations.Name;
import com.taobao.middleware.cli.annotations.Summary;
import qunar.tc.bistoury.attach.arthas.instrument.InstrumentClientStore;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * @author zhenyu.nie created on 2018 2018/11/30 19:52
 */
@Name("shutdown")
@Summary("Shut down bistoury server and exit the console")
public class QShutdownCommand extends AnnotatedCommand {

    @Override
    public void process(CommandProcess process) {
        try {
            // 退出之前需要重置所有的增强类
            try {
                EnhancerAffect enhancerAffect = clearArthasInstrument(process);
                process.write(enhancerAffect.toString()).write("\n");
            } catch (Exception e) {
                // ignore
            }

            try {
                InstrumentClientStore.destroy();
            } catch (Exception e) {
                // ignore
            }

            process.write("bistoury Server is going to shut down...\n");
        } finally {
            process.end();
            ShellServer server = process.session().getServer();
            server.close();
        }
    }

    private EnhancerAffect clearArthasInstrument(CommandProcess process) throws UnmodifiableClassException {
        Instrumentation inst = process.session().getInstrumentation();
        return Enhancer.reset(inst, new WildcardMatcher("*"));
    }

}
