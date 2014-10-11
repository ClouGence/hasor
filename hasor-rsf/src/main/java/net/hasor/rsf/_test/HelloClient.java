package net.hasor.rsf._test;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.hasor.rsf.general.ProtocolType;
import net.hasor.rsf.protocol.ProtocolRequest;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class HelloClient {
    public void connect(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new HelloClientIntHandler());
                }
            });
            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
    public static void main(String[] args) throws Exception {
        //
        //        byte[] data = "HelloWord...".getBytes("utf-8");
        //        CRC32 crc = new CRC32();
        //        crc.update(data);
        //        String a = Long.toString(crc.getValue(), 16);
        //        System.out.println(a.toUpperCase());
        //
        HelloClient client = new HelloClient();
        client.connect("127.0.0.1", 8000);
    }
}
class HelloClientIntHandler extends ChannelInboundHandlerAdapter {
    // 连接成功后，向server发送消息
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ProtocolRequest req = new ProtocolRequest();
        req.setRequestID(1234);
        req.setProtocolType(ProtocolType.Request);
        req.setSerializeType("json");
        req.setServiceName("java.util.List");
        req.setServiceVersion("1.0.0");
        req.setServiceGroup("default");
        req.setTargetMethod("size");
        //
        ByteBuf allData = PooledByteBufAllocator.DEFAULT.heapBuffer();
        req.encode(allData);
        byte[] allBytes = allData.readBytes(allData.readableBytes()).array();
        //
        for (int i = 0;; i++) {
            if ((i * 5) > allBytes.length) {
                break;
            }
            ByteBuf itemData = ctx.alloc().buffer();
            int length = ((i * 5) < allBytes.length) ? 5 : (allBytes.length - (i * 5));
            itemData.writeBytes(allBytes, i * 5, length);
            //
            //            ctx.alloc().buffer()
            ctx.write(itemData);
            ctx.flush();
            itemData.duplicate();
        }
        //
        System.out.println();
    }
}
