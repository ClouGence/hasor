package net.hasor.rsf._test;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.IOException;
import net.hasor.rsf.general.ProtocolVersion;
import net.hasor.rsf.net.netty.RSFCodec;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.runtime.client.netty.ClientHandler;
import net.hasor.rsf.serialize.coder.Hessian_DecoderEncoder;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Client {
    public void connect(String host, int port) throws Exception {
        final ServerRsfContext manager = new ServerRsfContext();
        try {
            Bootstrap b = new Bootstrap();
            b.group(manager.getLoopGroup());
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(//
                            new RSFCodec(),//
                            new ClientHandler(manager));
                }
            });
            //发起100万次调用.
            ChannelFuture f = b.connect(host, port).sync();
            {
                for (int i = 0; i < 1000000; i++) {
                    RequestMsg req = getData();
                    f.channel().writeAndFlush(req).await();
                }
            }
            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            manager.getLoopGroup().shutdownGracefully();
        }
    }
    //
    //
    //
    private static int reqID = 0;
    private RequestMsg getData() throws IOException {
        Hessian_DecoderEncoder de = new Hessian_DecoderEncoder();
        RequestMsg request = new RequestMsg();
        request.setVersion(ProtocolVersion.V_1_0.value());
        request.setRequestID(reqID++);
        //
        request.setServiceName("net.hasor.rsf._test.TestServices");
        request.setServiceVersion("1.0.0");
        request.setServiceGroup("default");
        request.setTargetMethod("sayHello");//String item, int index
        request.setSerializeType("Hessian");
        //
        request.addParameter("java.lang.String", de.encode("你好..."));
        //
        request.addOption("sync", "true");
        //
        return request;
    }
    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.connect("127.0.0.1", 8000);
    }
}