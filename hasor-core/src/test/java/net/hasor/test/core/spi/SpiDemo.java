package net.hasor.test.core.spi;
import net.hasor.core.BindInfo;
import net.hasor.core.Spi;
import net.hasor.core.spi.CreatorProvisionListener;

/**
 *
 * @version : 2014年9月7日
 * @author 赵永春 (zyc@hasor.net)
 */
@Spi(CreatorProvisionListener.class)
public class SpiDemo implements CreatorProvisionListener {
    private Object      newObject;
    private BindInfo<?> bindInfo;

    public Object getNewObject() {
        return newObject;
    }

    public BindInfo<?> getBindInfo() {
        return bindInfo;
    }

    @Override
    public void beanCreated(Object newObject, BindInfo<?> bindInfo) throws Throwable {
        this.newObject = newObject;
        this.bindInfo = bindInfo;
    }
}