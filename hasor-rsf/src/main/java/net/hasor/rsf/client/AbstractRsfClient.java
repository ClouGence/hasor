package net.hasor.rsf.client;
import java.util.concurrent.Future;
import net.hasor.rsf.server.RsfRequest;
import net.hasor.rsf.server.RsfResponse;
/**
 * 远程RSF服务器的客户端类。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class AbstractRsfClient implements RsfClient {
    //
    public Object syncInvoke(RsfRequest rsfRequest) throws Exception {
        RsfResponseFuture resFuture = this.sendRequest(rsfRequest);
        resFuture.await();
        return resFuture.get();
    }
    public Future<Object> asyncInvoke(RsfRequest rsfRequest) {
        RsfResponseFuture resFuture = this.sendRequest(rsfRequest);
        resFuture.await();
        return resFuture.get();
    }
    public void invokeWithCallBack(RsfRequest rsfRequest, final RsfCallBack listener) {
        Future<RsfResponse> future = this.sendRequest(rsfRequest);
    }
}