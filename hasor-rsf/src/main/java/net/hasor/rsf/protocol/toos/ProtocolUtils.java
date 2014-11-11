package net.hasor.rsf.protocol.toos;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.RSFConstants;
import net.hasor.rsf.protocol.block.RequestSocketBlock;
import net.hasor.rsf.protocol.block.ResponseSocketBlock;
import net.hasor.rsf.protocol.codec.Protocol;
import net.hasor.rsf.protocol.codec.RpcRequestProtocol;
import net.hasor.rsf.protocol.codec.RpcResponseProtocol;
/**
 * Protocol Interface,for custom network protocol
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProtocolUtils {
    private static Protocol<RequestSocketBlock>[]  reqProtocolPool = new Protocol[63];
    private static Protocol<ResponseSocketBlock>[] resProtocolPool = new Protocol[63];
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
    public static Protocol<RequestSocketBlock> requestProtocol(byte ver) {
        return reqProtocolPool[version(ver) - 1];
    }
    public static Protocol<ResponseSocketBlock> responseProtocol(byte ver) {
        return resProtocolPool[version(ver) - 1];
    }
    //
    //
    //
    /**判断 response 是否为一个ACK包。*/
    public static boolean isACK(ResponseSocketBlock socketMessage) {
        return socketMessage.getStatus() == ProtocolStatus.Accepted.shortValue();
    }
    /**是否为Request消息。*/
    public static boolean isRequest(byte version) {
        return (0xC1 | version) == version;
    }
    /**是否为Response消息。*/
    public static boolean isResponse(byte version) {
        return (0x81 | version) == version;
    }
    /**获取协议版本。*/
    public static byte getVersion(byte version) {
        return (byte) (0x3F & version);
    }
    /**生成 request 的 version 信息。*/
    public static byte finalVersionForRequest(byte version) {
        return (byte) (RSFConstants.RSF_Request | version);
    }
    /**生成 response 的 version 信息。*/
    public static byte finalVersionForResponse(byte version) {
        return (byte) (RSFConstants.RSF_Response | version);
    }
}