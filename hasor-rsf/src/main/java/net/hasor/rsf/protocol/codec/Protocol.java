package net.hasor.rsf.protocol.codec;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
/**
 * Protocol Interface,for custom network protocol
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public interface Protocol<T> {
    /**encode Message to byte & write to network framework*/
    public void encode(T message, ByteBuf buf) throws IOException;
    /**decode stream to object*/
    public T decode(ByteBuf buf) throws IOException;
}