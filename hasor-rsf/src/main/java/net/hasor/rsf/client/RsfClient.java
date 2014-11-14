package net.hasor.rsf.client;
import java.util.Map;
import java.util.concurrent.Future;
import net.hasor.rsf.server.RsfRequest;
import net.hasor.rsf.server.RsfResponse;
/**
 * 远程RSF服务器的客户端类。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfClient {
    private String              remoteHost = null;
    private int                 remotePort = 0;
    private String              localHost  = null;
    private int                 localPort  = 0;
    private Map<String, String> optionMap  = null; //选项
    //
    /**server address.*/
    public String getServerHost() {
        return this.remoteHost;
    }
    /**server port.*/
    public int getServerPort() {
        return this.remotePort;
    }
    public String getLocalHost() {
        return this.localHost;
    }
    public int getLocalPort() {
        return this.localPort;
    }
    /**获取选项Key集合。*/
    public String[] getOptionKeys() {
        return this.optionMap.keySet().toArray(new String[this.optionMap.size()]);
    }
    /**获取选项数据*/
    public String getOption(String key) {
        return this.optionMap.get(key);
    }
    /**设置选项数据*/
    public void addOption(String key, String value) {
        this.optionMap.put(key, value);
    }
    /**connect timeout.*/
    public int getConnectTimeout();
    //
    //
    //
    //
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
     
    //
    public RsfResponseFuture sendRequest(RsfRequest rsfRequest);
    //
    public void close();
    public boolean isDone();
    public boolean isConnected();
}