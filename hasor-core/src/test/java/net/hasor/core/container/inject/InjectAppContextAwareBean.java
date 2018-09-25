package net.hasor.core.container.inject;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.container.beans.CallInitBean;
public class InjectAppContextAwareBean extends CallInitBean implements AppContextAware {
    @Override
    public void setAppContext(AppContext appContext) {
        super.init();
    }
}
