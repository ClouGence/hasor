package net.hasor.test.core.docs;
import net.hasor.core.AppContext;
import net.hasor.core.Inject;
import net.hasor.core.spi.InjectMembers;

public class OrderManager implements InjectMembers {
    @Inject  // <-因为实现了InjectMembers接口，因此@Inject注解将会失效。
    public CustomBean stockBeanTest;
    public CustomBean stockBean;

    public void doInject(AppContext appContext) throws Throwable {
        //
    }

    public CustomBean getStockBeanTest() {
        return stockBeanTest;
    }

    public CustomBean getStockBean() {
        return stockBean;
    }
}