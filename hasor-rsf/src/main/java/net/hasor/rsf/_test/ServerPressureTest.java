package net.hasor.rsf._test;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.io.IOException;
import java.net.InetAddress;
import net.hasor.rsf.general.ProtocolStatus;
import net.hasor.rsf.general.ProtocolVersion;
import net.hasor.rsf.net.netty.RSFCodec;
import net.hasor.rsf.protocol.message.RequestMsg;
import net.hasor.rsf.protocol.message.ResponseMsg;
import net.hasor.rsf.serialize.coder.HessianSerializeCoder;
/**
 * 对Server的压力测试
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerPressureTest {
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
            f.channel().closeFuture().sync();
        } finally {
            manager.getLoopGroup().shutdownGracefully();
        }
    }
    //
    public static void main(String[] args) throws Exception {
        for (int i=1 ;i<10;i++){
            new Thread(){
                public void run() {
                    try {
                        ServerPressureTest client = new ServerPressureTest();
                        client.connect(InetAddress.getLocalHost().getHostAddress(), 8000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                };
            }.start();
        }

    }
}
class ClientHandler extends ChannelInboundHandlerAdapter {
    private ServerRsfContext manager          = null;
    private long             sendCount        = 0;
    private long             acceptedCount    = 0;
    private long             chooseOtherCount = 0;
    private long             serializeError   = 0;
    private long             requestTimeout   = 0;
    private long             okCount          = 0;
    private long             start            = System.currentTimeMillis();
    //
    public ClientHandler(ServerRsfContext manager) {
        this.manager = manager;
        manager.getCallExecute("aa").execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    aa();
                    try {
                        Thread.sleep(3000);
                    } catch (Exception e) {}
                }
            }
        });
    }
    public void aa() {
        long duration = System.currentTimeMillis() - start;
        System.out.println("send QPS  :" + (sendCount * 1000 / duration));
        System.out.println("accept QPS:" + ((acceptedCount - chooseOtherCount) * 1000 / duration));
        System.out.println("send      :" + sendCount);
        System.out.println("accept    :" + (acceptedCount - chooseOtherCount));
        System.out.println("choose    :" + chooseOtherCount);
        System.out.println("serialize :" + serializeError);
        System.out.println("timeout   :" + requestTimeout);
        System.out.println("ok(%)     :" + okCount);
        System.out.println();
    }
    //
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ResponseMsg response = (ResponseMsg) msg;
        //
        if (response.getStatus() == ProtocolStatus.Accepted)
            acceptedCount++;
        else if (response.getStatus() == ProtocolStatus.ChooseOther)
            chooseOtherCount++;
        else if (response.getStatus() == ProtocolStatus.OK)
            okCount++;
        else if (response.getStatus() == ProtocolStatus.SerializeError)
            serializeError++;
        else if (response.getStatus() == ProtocolStatus.RequestTimeout)
            requestTimeout++;
        else {
            int a = 0;
            a++;
        }
    }
    //1.第一个包
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelFutureListener listener = new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess() == false)
                    return;
                future.channel().writeAndFlush(getData()).addListener(this);
            }
        };
        //
        ctx.writeAndFlush(getData()).addListener(listener);
    }
    //
    //
    private static int reqID = 0;
    private RequestMsg getData() throws IOException {
        HessianSerializeCoder de = new HessianSerializeCoder();
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
        sendCount++;
        return request;
    }
}