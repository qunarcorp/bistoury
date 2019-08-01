package qunar.tc.bistoury.remoting.protocol;

import qunar.tc.bistoury.remoting.protocol.payloadHolderImpl.ErrorResponsePayloadHolder;

/**
 * @author leix.xie
 * @date 2019/5/13 14:11
 * @describe
 */
public class RemotingBuilder {

    public static Datagram buildRequestDatagram(final int code, final String id, final PayloadHolder payloadHolder) {
        final Datagram datagram = new Datagram();
        datagram.setHeader(buildRemotingHeader(code, id));
        datagram.setPayloadHolder(payloadHolder);
        return datagram;
    }

    private static RemotingHeader buildRemotingHeader(final int code, final String id) {
        RemotingHeader header = new RemotingHeader();
        header.setCode(code);
        header.setId(id);
        header.setVersion(RemotingHeader.PROTOCOL_VERSION);
        header.setAgentVersion(RemotingHeader.AGENT_VERSION);
        header.setFlag(RemotingHeader.DEFAULT_FLAG);
        return header;
    }

    public static Datagram buildResponseDatagram(final int code, final RemotingHeader requestHeader, final PayloadHolder payloadHolder) {
        final Datagram datagram = new Datagram();
        datagram.setHeader(buildRemotingHeader(code, requestHeader));
        datagram.setPayloadHolder(payloadHolder);
        return datagram;
    }

    public static Datagram buildErrorResponseDatagram(final int errorCode, final String message) {
        final Datagram datagram = new Datagram();
        RemotingHeader header = new RemotingHeader();
        header.setCode(ResponseCode.RESP_TYPE_EXCEPTION.getCode());
        header.setFlag(RemotingHeader.DEFAULT_FLAG);
        datagram.setHeader(header);
        datagram.setPayloadHolder(new ErrorResponsePayloadHolder(errorCode, message));
        return datagram;
    }

    public static Datagram buildEmptyResponseDatagram(final int code, final RemotingHeader requestHeader) {
        return buildResponseDatagram(code, requestHeader, null);
    }

    private static RemotingHeader buildRemotingHeader(final int code, final RemotingHeader requestHeader) {
        RemotingHeader header = new RemotingHeader();
        header.setCode(code);
        header.setVersion(RemotingHeader.PROTOCOL_VERSION);
        header.setAgentVersion(RemotingHeader.AGENT_VERSION);
        header.setFlag(RemotingHeader.DEFAULT_FLAG);
        header.setId(requestHeader.getId());
        return header;
    }

    public static Datagram buildAgentRequest(final int code, PayloadHolder holder) {
        final Datagram datagram = new Datagram();
        datagram.setHeader(buildRemotingHeader(code, ""));
        datagram.setPayloadHolder(holder);
        return datagram;
    }
}
