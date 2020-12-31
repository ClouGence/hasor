package net.hasor.test.core.spi;
import net.hasor.core.AppContext;
import net.hasor.core.spi.ContextInitializeListener;

/**
 *
 * @version : 2014年9月7日
 * @author 赵永春 (zyc@hasor.net)
 */
public class JdkSpiImpl implements ContextInitializeListener {
    private static boolean init = false;

    public static boolean isInit() {
        return init;
    }

    public static void resetInit() {
        init = false;
    }

    @Override
    public void doInitializeCompleted(AppContext templateAppContext) {
        init = true;
    }
}
