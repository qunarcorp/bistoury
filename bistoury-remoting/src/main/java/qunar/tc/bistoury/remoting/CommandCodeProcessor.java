package qunar.tc.bistoury.remoting;

import com.google.common.base.Preconditions;
import io.netty.channel.ChannelHandlerContext;
import qunar.tc.bistoury.remoting.protocol.RemotingHeader;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author zhenyu.nie created on 2017 2017/8/24 11:49
 */
public abstract class CommandCodeProcessor<T> {

    private final Class<T> type;

    @SuppressWarnings("all")
    public CommandCodeProcessor() {
        Type superClass = this.getClass().getGenericSuperclass();
        Preconditions.checkArgument(!(superClass instanceof Class) && superClass instanceof ParameterizedType && ((ParameterizedType) superClass).getRawType() == CommandCodeProcessor.class, "[%s]必须是[%s]的子类并且确定了泛型参数");
        ParameterizedType t = (ParameterizedType) superClass;
        Type theType = t.getActualTypeArguments()[0];
        Preconditions.checkArgument(theType instanceof Class && ((Class) theType).getTypeParameters().length == 0, "[%s]参数必须是个非泛型类", theType);
        this.type = (Class<T>) theType;
    }

    public abstract int code();

    public final Class<T> type() {
        return type;
    }

    @SuppressWarnings("all")
    public final void process(RemotingHeader header, Object body, ChannelHandlerContext context) {
        if (body != null && !type.isInstance(body)) {
            throw new RuntimeException("illegal type [" + body.getClass() + "] for [" + type + "]");
        }

        doProcess(header, (T) body, context);
    }

    protected abstract void doProcess(RemotingHeader header, T body, ChannelHandlerContext ctx);
}
