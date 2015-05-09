package test.net.hasor.rsf.customer._01_echo;
import net.hasor.core.Settings;
import net.hasor.rsf.RsfBinder;
import net.hasor.rsf.RsfBinder.RegisterBuilder;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.RsfFilter;
import net.hasor.rsf.bootstrap.RsfBootstrap;
import net.hasor.rsf.bootstrap.RsfStart;
import net.hasor.rsf.bootstrap.WorkMode;
import org.more.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.net.hasor.rsf.service.EchoService;
/**
 * 
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfCustomer {
    protected static Logger logger = LoggerFactory.getLogger(RsfCustomer.class);
    public static RsfContext build(final RsfFilter qpsFilter, final Settings settings) throws Throwable { //启动客户端
        RsfBootstrap boot = new RsfBootstrap();
        final RsfContext rsfContext = boot.bindSettings(settings).doBinder(new RsfStart() {
            public void onBind(RsfBinder rsfBinder) throws Throwable {
                rsfBinder.bindFilter("QoS", qpsFilter);
                // 
                //获取服务地址
                final String serverURL = settings.getString("testServerURL");
                logger.info("serverURL:" + serverURL);
                //
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
        return rsfContext;
    }
}