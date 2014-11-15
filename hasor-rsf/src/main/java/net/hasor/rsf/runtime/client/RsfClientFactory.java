package net.hasor.rsf.runtime.client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import net.hasor.core.Hasor;
import net.hasor.rsf.general.SendLimitPolicy;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.net.netty.NetworkChanne;
import net.hasor.rsf.net.netty.RSFCodec;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.client.netty.ClientHandler;
import net.hasor.rsf.runtime.client.netty.NettyRsfClient;
/**
 * 负责创建{@link RsfClient}。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfClientFactory {
    private SendLimitPolicy sendLimitPolicy = SendLimitPolicy.Reject;
    private RsfContext      rsfContext      = null;
    //
    public RsfClientFactory(RsfContext rsfContext) {
        Hasor.assertIsNotNull(rsfContext, "rsfContext is null.");
        this.rsfContext = rsfContext;
        this.sendLimitPolicy = rsfContext.getSendLimitPolicy();
    }
    //
    /**连接远程服务（分布式）*/
    public RsfClient connect(String serviceName) {
        return connect(this.rsfContext.getService(serviceName));
    }
    /**连接远程服务（分布式）*/
    public RsfClient connect(ServiceMetaData metaData) {
        //
    }
    /**连接远程服务（具体的地址）*/
    public RsfClient connect(String hostName, int port) {
        return connect(new InetSocketAddress(hostName, port));
    }
    /**连接远程服务（具体的地址）*/
    public RsfClient connect(SocketAddress remoteAddress) {
        return connect(remoteAddress, null);
    }
    /**连接远程服务（具体的地址）*/
    public RsfClient connect(SocketAddress remoteAddress, SocketAddress localAddress) {
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