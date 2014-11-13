package net.hasor.rsf.client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.SocketAddress;
import net.hasor.core.Hasor;
import net.hasor.rsf.context.RsfContext;
import net.hasor.rsf.metadata.ServiceMetaData;
import net.hasor.rsf.net.netty.RSFCodec;
import net.hasor.rsf.server.RsfRequest;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfClient {
    private RsfContext rsfContext = null;
    public RsfClient(RsfContext rsfContext) {
        this.rsfContext = rsfContext;
    }
    //
    public RsfRequest createRequest(ServiceMetaData metaData, SocketAddress remoteAddress) {
        return createRequest(metaData, remoteAddress, null);
    }
    public RsfRequest createRequest(ServiceMetaData metaData, SocketAddress remoteAddress, SocketAddress localAddress) {
        Hasor.assertIsNotNull(metaData, "metaData is null.");
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
        return new RsfRequestImpl(metaData, future.channel(), this.rsfContext);
    }
}