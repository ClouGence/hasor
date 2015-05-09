package test.net.hasor.rsf.customer;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.plugins.monitor.QpsMonitor;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class CustomerClient {
    public static void main(String[] args) throws Throwable {
        //因为没有注册中心，由配置文件来代替。
        StandardContextSettings setting = new StandardContextSettings("rsf-config.xml");
        setting.refresh();
        //监视器
        RsfFilter[] plugins = new RsfFilter[] { new QpsMonitor() };
        //获取准备启动的客户端数量
        int clientCount = setting.getInteger("testConfig.clientCount");
        for (int chientID = 0; chientID < clientCount; chientID++) {
            new ServiceTestClient(plugins).startClient(chientID, setting);
        }
    }
}