package net.hasor.core.container.aware;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
public class AppContextAwareBean implements AppContextAware {
    private AppContext appContext;
    public AppContext getAppContext() {
        return appContext;
    }
    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
}