package net.hasor.rsf.runtime.client;
import java.util.HashMap;
import java.util.Map;
import net.hasor.rsf.net.netty.NetworkChanne;
/**
 * 远程RSF服务器的客户端类。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class AbstractRsfClient implements RsfClient {
    private Map<String, String> optionMap = new HashMap<String, String>();
    //
    /**server address.*/
    public String getServerHost() {
        return this.getNetworkChanne().getRemotHost();
    }
    /**server port.*/
    public int getServerPort() {
        return this.getNetworkChanne().getRemotePort();
    }
    public String getLocalHost() {
        return this.getNetworkChanne().getLocalHost();
    }
    public int getLocalPort() {
        return this.getNetworkChanne().getLocalPort();
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
    //
    /**关闭与远端的连接*/
    public void close() throws InterruptedException {
        this.getNetworkChanne().close().await();
    }
    /**连接是否为活动的。*/
    public boolean isActive() {
        return this.getNetworkChanne().isActive();
    }
    //
    /**获取网络连接*/
    protected abstract NetworkChanne getNetworkChanne();
}