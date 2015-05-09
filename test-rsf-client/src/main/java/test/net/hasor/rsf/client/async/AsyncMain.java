package test.net.hasor.rsf.client.async;
import net.hasor.core.setting.StandardContextSettings;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.plugins.monitor.QpsMonitor;
import test.net.hasor.rsf.customer._01_echo.RsfCustomer;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class AsyncMain {
    public static void main(String[] args) throws Throwable {
        //
        //因为没有注册中心，由配置文件来代替。
        QpsMonitor qps = new QpsMonitor();
        StandardContextSettings setting = new StandardContextSettings("rsf-config.xml");
        setting.refresh();
        //获取准备启动的客户端数量
        int clientCount = setting.getInteger("testConfig.clientCount");
        for (int chientID = 0; chientID < clientCount; chientID++) {
            RsfContext rsfContext = RsfCustomer.build(qps, setting);
            new AsyncClient(chientID, rsfContext).syncRun();
        }
    }
}