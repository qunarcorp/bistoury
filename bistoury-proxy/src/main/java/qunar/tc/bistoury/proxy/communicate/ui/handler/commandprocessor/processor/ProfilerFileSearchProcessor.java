//package qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.processor;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.google.common.base.Charsets;
//import com.google.common.collect.ImmutableSet;
//import io.netty.buffer.ByteBuf;
//import qunar.tc.bistoury.common.JacksonSerializer;
//import qunar.tc.bistoury.common.TypeResponse;
//import qunar.tc.bistoury.proxy.communicate.ui.handler.commandprocessor.AbstractCommand;
//import qunar.tc.bistoury.remoting.command.ProfilerSearchCommand;
//import qunar.tc.bistoury.remoting.protocol.CommandCode;
//import qunar.tc.bistoury.remoting.protocol.Datagram;
//import qunar.tc.bistoury.serverside.dao.ProfilerDao;
//import qunar.tc.bistoury.serverside.dao.ProfilerDaoImpl;
//
//import java.util.Set;
//
///**
// * @author cai.wen created on 2019/10/25 16:46
// */
//public class ProfilerFileSearchProcessor extends AbstractCommand<ProfilerSearchCommand> {
//
//    private final ProfilerDao profilerDao = new ProfilerDaoImpl();
//
//    @Override
//    public Set<Integer> getCodes() {
//        return ImmutableSet.of(CommandCode.REQ_TYPE_PROFILER_FINISH_STATE_SEARCH.getCode());
//    }
//
//    @Override
//    public int getMinAgentVersion() {
//        return -1;
//    }
//
//    @Override
//    public Datagram prepareResponse(Datagram datagram) {
//        String profilerId = datagram.getHeader().getId();
//        TypeResponse<String> response = getTypeResponse(datagram.getBody());
//        if (response.getData().getCode() == 0) {
//            profilerDao.stopProfiler(profilerId);
//        }
//        return super.prepareResponse(datagram);
//    }
//
//    private TypeResponse<String> getTypeResponse(ByteBuf body) {
//        byte[] data = new byte[body.readableBytes()];
//        body.readBytes(data);
//        String response = new String(data, Charsets.UTF_8);
//        return JacksonSerializer.deSerialize(response, new TypeReference<TypeResponse<String>>() {
//        });
//    }
//
//    @Override
//    public boolean supportMulti() {
//        return false;
//    }
//}
