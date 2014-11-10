package net.hasor.rsf.protocol.codec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.io.IOException;
import net.hasor.rsf.protocol.message.ResponseSocketMessage;
/**
 * Protocol Interface,for custom network protocol
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class RpcResponseProtocol implements Protocol<ResponseSocketMessage> {
    /**encode Message to byte & write to network framework*/
    public void encode(ResponseSocketMessage resMsg, ByteBuf buf) throws IOException {
        //
        //* --------------------------------------------------------bytes =13
        //* byte[1]  version                              RSF版本(0x81)
        buf.writeByte(resMsg.getVersion());
        //* byte[8]  requestID                            请求ID
        buf.writeLong(resMsg.getRequestID());
        //* byte[4]  contentLength                        内容大小(max = 16MB)
        ByteBuf responseBody = this.encodeResponse(resMsg);
        int bodyLength = responseBody.readableBytes();
        bodyLength = (bodyLength << 8) >>> 8;//左移8未，在无符号右移8位。形成最大16777215字节的限制。
        buf.writeInt(bodyLength);
        //
        buf.writeBytes(responseBody);
        //
    }
    //
    private ByteBuf encodeResponse(ResponseSocketMessage resMsg) {
        ByteBuf bodyBuf = ByteBufAllocator.DEFAULT.heapBuffer();
        //
        //* --------------------------------------------------------bytes =8
        //* byte[2]  status                               响应状态
        bodyBuf.writeShort(resMsg.getStatus());
        //* byte[2]  serializeType-(attr-index)           序列化策略
        bodyBuf.writeShort(resMsg.getSerializeType());
        //* byte[2]  returnType-(attr-index)              返回类型
        bodyBuf.writeShort(resMsg.getReturnType());
        //* byte[2]  returnData-(attr-index)              返回数据
        bodyBuf.writeShort(resMsg.getReturnData());
        //* --------------------------------------------------------bytes =1 ~ 1021
        //* byte[1]  optionCount                          选项参数总数
        int[] optionMapping = resMsg.getOptions();
        bodyBuf.writeByte(optionMapping.length);
        for (int i = 0; i < optionMapping.length; i++) {
            //* byte[4]  ptype-0-(attr-index,attr-index)  选项参数1
            //* byte[4]  ptype-1-(attr-index,attr-index)  选项参数2
            bodyBuf.writeInt(optionMapping[i]);
        }
        //* --------------------------------------------------------bytes =6 ~ 8192
        //* byte[2]  attrPool-size (Max = 2047)           池大小 0x07FF
        int[] poolData = resMsg.getPoolData();
        bodyBuf.writeShort(poolData.length);
        for (int i = 0; i < poolData.length; i++) {
            //* byte[4]  ptype-0-(attr-index,attr-index)  属性1大小
            //* byte[4]  ptype-1-(attr-index,attr-index)  属性2大小
            bodyBuf.writeInt(poolData[i]);
        }
        //* --------------------------------------------------------bytes =n
        //* dataBody                                      数据内容
        resMsg.fillTo(bodyBuf);
        return bodyBuf;
    }
    //
    //
    //
    /**decode stream to object*/
    public ResponseSocketMessage decode(ByteBuf buf) throws IOException {
        //* --------------------------------------------------------bytes =13
        //* byte[1]  version                              RSF版本(0x80)
        byte version = buf.readByte();
        //* byte[8]  requestID                            包含的请求ID
        long requestID = buf.readLong();
        //* byte[4]  contentLength                        内容大小
        buf.skipBytes(4);
        //
        ResponseSocketMessage res = new ResponseSocketMessage();
        res.setVersion(version);
        res.setRequestID(requestID);
        //* --------------------------------------------------------bytes =8
        //* byte[2]  status                               响应状态
        res.setStatus(buf.readShort());
        //* byte[2]  serializeType-(attr-index)           序列化策略
        res.setSerializeType(buf.readShort());
        //* byte[2]  returnType-(attr-index)              返回类型
        res.setReturnType(buf.readShort());
        //* byte[2]  returnData-(attr-index)              返回数据
        res.setReturnData(buf.readShort());
        //* --------------------------------------------------------bytes =1 ~ 1021
        //* byte[1]  optionCount                          选项参数总数
        byte optionCount = buf.readByte();
        for (int i = 0; i < optionCount; i++) {
            //* byte[4]  attr-0-(attr-index,attr-index)   选项参数
            int mergeData = buf.readInt();
            res.addOption(mergeData);
        }
        //* --------------------------------------------------------bytes =6 ~ 8192
        //* byte[2]  attrPool-size (Max = 2047)           池大小
        short attrPoolSize = buf.readShort();
        for (int i = 0; i < attrPoolSize; i++) {
            //* byte[4] att-length                        属性1大小
            int length = buf.readInt();
            res.addPoolData(length);
        }
        //* --------------------------------------------------------bytes =n
        //* dataBody                                      数据内容
        res.fillFrom(buf.readBytes(res.getPoolSize()));
        return res;
    }
}