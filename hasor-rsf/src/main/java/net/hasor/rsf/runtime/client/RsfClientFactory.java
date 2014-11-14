package net.hasor.rsf.runtime.client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.SocketAddress;
import net.hasor.core.Hasor;
import net.hasor.rsf.net.netty.NetworkChanne;
import net.hasor.rsf.net.netty.RSFCodec;
import net.hasor.rsf.runtime.client.netty.ClientHandler;
import net.hasor.rsf.runtime.client.netty.NettyRsfClient;
import net.hasor.rsf.runtime.context.RsfContext;
/**
 * 负责创建{@link RsfClient}。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfClientFactory {
    private RsfContext rsfContext = null;
    public RsfClientFactory(RsfContext rsfContext) {
        Hasor.assertIsNotNull(rsfContext, "rsfContext is null.");
        this.rsfContext = rsfContext;
    }
    //
    public RsfClient newClient(SocketAddress remoteAddress) {
        return newClient(remoteAddress, null);
    }
    public RsfClient newClient(SocketAddress remoteAddress, SocketAddress localAddress) {
        Hasor.assertIsNotNull(remoteAddress, "remoteAddress is null.");
        //
        Bootstrap boot = new Bootstrap();
        boot.group(this.rsfContext.getLoopGroup());
        boot.channel(NioSocketChannel.class);
        boot.option(ChannelOption.SO_KEEPALIVE, true);
        final RsfContext rsfContext = this.rsfContext;
        boot.handler(new ChannelInitializer<SocketChannel>() {
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RSFCodec(), new ClientHandler(rsfContext));
            }
        });
        ChannelFuture future = null;
        if (localAddress != null) {
            future = boot.connect(remoteAddress, localAddress);
        } else {
            future = boot.connect(remoteAddress);
        }
        //
        NetworkChanne connection = new NetworkChanne(future.channel());
        return new NettyRsfClient(connection, this.rsfContext);
    }
}