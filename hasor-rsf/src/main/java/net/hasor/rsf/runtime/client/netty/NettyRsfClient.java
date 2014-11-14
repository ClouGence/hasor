package net.hasor.rsf.runtime.client.netty;
import java.util.concurrent.Future;
import net.hasor.rsf.net.netty.NetworkChanne;
import net.hasor.rsf.runtime.RsfContext;
import net.hasor.rsf.runtime.RsfRequest;
import net.hasor.rsf.runtime.RsfResponse;
import net.hasor.rsf.runtime.client.AbstractRsfClient;
import net.hasor.rsf.runtime.client.RsfCallBack;
/**
 * 远程RSF服务器的客户端类。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class NettyRsfClient extends AbstractRsfClient {
    public NettyRsfClient(NetworkChanne connection, RsfContext rsfContext) {
        // TODO Auto-generated constructor stub
    }
    //
    public Object syncInvoke(RsfRequest rsfRequest) {
        RsfResponseFuture resFuture = this.sendRequest(rsfRequest);
        resFuture.await();
        return resFuture.get().getReturn();
    }
    public Future<Object> asyncInvoke(RsfRequest rsfRequest) {
        RsfResponseFuture resFuture = this.sendRequest(rsfRequest);
        resFuture.await();
        return resFuture.get();
    }
    public void invokeWithCallBack(RsfRequest rsfRequest, final RsfCallBack listener) {
        Future<RsfResponse> future = this.sendRequest(rsfRequest);
    }
    //
    public RsfResponseFuture sendRequest(RsfRequest rsfRequest);
    //
    @Override
    protected NetworkChanne getNetworkChanne() {
        // TODO Auto-generated method stub
        return null;
    }
}