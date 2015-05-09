package test.net.hasor.rsf.customer;
import net.hasor.core.Settings;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfBinder.RegisterBuilder;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.RsfStart;
import net.hasor.rsf.bootstrap.WorkMode;
import org.more.logger.LoggerHelper;
import org.more.util.StringUtils;
import test.net.hasor.rsf.service.EchoService;
/**
 * 一个客户端打开200个线程进行同步远程调用。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceTestClient {
    private final RsfFilter[] rsfFilter;
    public ServiceTestClient(RsfFilter[] rsfFilter) {
        this.rsfFilter = (rsfFilter == null) ? new RsfFilter[0] : rsfFilter;
    }
    //
    public void startClient(final int chientID, final Settings settings) throws Throwable {
        //获取服务地址
        final String serverURL = settings.getString("testServerURL");
        LoggerHelper.logInfo("serverURL:" + serverURL);
        //启动客户端
        RsfBootstrap boot = new RsfBootstrap();
        final RsfContext rsfContext = boot.bindSettings(settings).doBinder(new RsfStart() {
            public void onBind(RsfBinder rsfBinder) throws Throwable {
                for (RsfFilter filter : rsfFilter) {
                    rsfBinder.bindFilter("QoS", filter);
                }
                RegisterBuilder<?> builder = rsfBinder.rsfService(EchoService.class);
                String[] address = serverURL.split(",");
                for (String addr : address) {
                    addr = addr.trim();
                    if (StringUtils.isBlank(addr))
                        continue;
                    builder = builder.bindAddress(addr);
                }
                //
                builder.register();
            }
        }).workAt(WorkMode.Customer).sync();
        //启动调用线程
        int threadCount = rsfContext.getSettings().getInteger("testConfig.threadCount");
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            new Thread() {
                public void run() {
                    String tName = chientID + "-T-" + index;
                    doCall(tName, rsfContext);
                };
            }.start();
        }
        System.out.println(rsfContext);
    }
    //
    private static void doCall(String threadName, RsfContext rsfContext) {
        RsfClient rsfClient = rsfContext.getRsfClient();
        RsfBindInfo<EchoService> bindInfo = rsfContext.getBindCenter().getService(EchoService.class);
        EchoService remoteService = rsfClient.getRemote(bindInfo);
        //
        int i = 0;
        String sayMessage = "sayData:" + threadName;
        while (true) {
            Object result = remoteService.echo(sayMessage, i++);
            if (i % 50000 == 0) {
                System.out.println(i + "\t" + result);
            }
        }
    }
}