package test.net.hasor.rsf.rpc.provider;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.util.List;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.domain.ServiceDomain;
import net.hasor.rsf.protocol.netty.RSFCodec;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ProviderClient {
    public void connect(String host, int port) throws Exception {
        RsfSettings settings = new DefaultRsfSettings(new StandardContextSettings());
        settings.refresh();
        final TestClientRsfContext rsfContext = new TestClientRsfContext(settings);
        final ServiceDomain domain = new ServiceDomain(List.class);
        domain.setBindGroup("RSF");
        //
        //
        //
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new RSFCodec(), new ClientHandler(rsfContext, domain));
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
        ProviderClient client = new ProviderClient();
        client.connect("127.0.0.1", 8000);
    }
}