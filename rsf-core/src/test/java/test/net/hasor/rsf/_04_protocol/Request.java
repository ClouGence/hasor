package test.net.hasor.rsf._04_protocol;
import java.io.IOException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import net.hasor.rsf.domain.RSFConstants;
import net.hasor.rsf.protocol.codec.Protocol;
import net.hasor.rsf.protocol.protocol.RequestSocketBlock;
import net.hasor.rsf.utils.ProtocolUtils;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Request {
    public void request() throws IOException {
        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();
        //
        RequestSocketBlock block = new RequestSocketBlock();
        block.setServiceGroup(ProtocolUtils.pushString(block, "group"));
        //
        Protocol<RequestSocketBlock> request = ProtocolUtils.requestProtocol(RSFConstants.Version_1);
        request.encode(block, buf);
    }
}