package test.net.hasor.rsf._04_protocol.socket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.hasor.core.Settings;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.rpc.context.DefaultRsfSettings;
import net.hasor.rsf.transform.netty.RSFCodec;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class Client {
    public void connect(String host, int port) throws Exception {
        Settings setting = new StandardContextSettings();//create Settings
        setting.refresh();
        final RsfSettings rsfSetting = new DefaultRsfSettings(setting);//create RsfSettings
        //
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new RSFCodec(), new ClientHandler(rsfSetting));
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
        Client client = new Client();
        client.connect("127.0.0.1", 8000);
    }
}