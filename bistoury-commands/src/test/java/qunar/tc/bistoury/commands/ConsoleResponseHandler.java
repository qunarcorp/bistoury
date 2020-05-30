package qunar.tc.bistoury.commands;

import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import qunar.tc.bistoury.agent.common.ResponseHandler;
import qunar.tc.bistoury.remoting.protocol.ErrorCode;

import java.util.Map;

/**
 * @author zhenyu.nie created on 2018 2018/10/16 11:03
 */
public class ConsoleResponseHandler implements ResponseHandler {

    @Override
    public boolean isWritable() {
        return true;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void handle(String line) {
        System.out.print(line);
    }

    @Override
    public void handle(int code, String line) {
        System.out.println(line);
    }

    @Override
    public void handle(int code, byte[] data) {
        System.out.println(data);
    }

    @Override
    public void handle(byte[] dataBytes) {
        System.out.print(dataBytes);
    }

    @Override
    public void handleError(int errorCode) {
        Optional<ErrorCode> optional = ErrorCode.valueOf(errorCode);
        if (optional.isPresent()) {
            System.out.println(optional.get().getMessage());
        } else {
            System.out.println("error: " + errorCode);
        }
    }

    @Override
    public void handleError(String error) {
        System.out.println(error);
    }

    @Override
    public void handleError(Throwable throwable) {
        System.out.print(throwable.getClass().getName() + ": " + throwable.getMessage());
    }

    @Override
    public void handleEOF() {
        System.out.print("exit");
    }

    @Override
    public void handleEOF(int exitCode) {
        System.out.print("exit " + exitCode);
    }

    @Override
    public void handle(int code, byte[] data, Map<String, String> responseHeader) {
        System.out.println("header: " + responseHeader + " code: " + code + " data: " + BaseEncoding.base16().lowerCase().encode(data));
    }
}
