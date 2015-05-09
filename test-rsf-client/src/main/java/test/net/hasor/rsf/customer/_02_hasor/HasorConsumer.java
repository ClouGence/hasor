package test.net.hasor.rsf.customer._02_hasor;
import net.hasor.rsf.RsfBinder.RegisterBuilder;
import net.hasor.rsf.plugins.hasor.RsfApiBinder;
import net.hasor.rsf.plugins.hasor.RsfModule;
import net.hasor.rsf.plugins.monitor.QpsMonitor;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.net.hasor.rsf.service.EchoService;
/**
 * 使用 Hasor 插件形式发布 RSF 服务。
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class HasorConsumer extends RsfModule {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        //获取服务地址
        final String serverURL = apiBinder.getEnvironment().getSettings().getString("testServerURL");
        logger.info("serverURL:" + serverURL);
        //
        apiBinder.getRsfBinder().bindFilter("QoS", new QpsMonitor());
        RegisterBuilder<?> builder = apiBinder.getRsfBinder().rsfService(EchoService.class);
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
}