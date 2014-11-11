package net.hasor.rsf.protocol.toos;
import net.hasor.rsf.protocol.codec.Protocol;
import net.hasor.rsf.protocol.codec.RpcRequestProtocol;
import net.hasor.rsf.protocol.codec.RpcResponseProtocol;
import net.hasor.rsf.protocol.message.RequestSocketMessage;
import net.hasor.rsf.protocol.message.ResponseSocketMessage;
/**
 * Protocol Interface,for custom network protocol
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProtocolUtils {
    private static Protocol<RequestSocketMessage>[]  reqProtocolPool = new Protocol[63];
    private static Protocol<ResponseSocketMessage>[] resProtocolPool = new Protocol[63];
    //
    static {
        reqProtocolPool[0] = new RpcRequestProtocol();
        resProtocolPool[0] = new RpcResponseProtocol();
    }
    /**获取协议版本。*/
    private static byte version(byte version) {
        return (byte) (0x3F & version);
    }
    //
    public static Protocol<RequestSocketMessage> requestProtocol(byte ver) {
        return reqProtocolPool[version(ver) - 1];
    }
    public static Protocol<ResponseSocketMessage> responseProtocol(byte ver) {
        return resProtocolPool[version(ver) - 1];
    }
}