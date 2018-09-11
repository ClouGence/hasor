package test.net.hasor.core.binder;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.LifeModule;
public class EmptyLifeModule implements LifeModule {
    private boolean doStart = false;
    private boolean doStop  = false;
    private boolean doLoad  = false;
    public boolean isDoStart() {
        return doStart;
    }
    public boolean isDoStop() {
        return doStop;
    }
    public boolean isDoLoad() {
        return doLoad;
    }
    @Override
    public void onStart(AppContext appContext) throws Throwable {
        this.doStart = true;
    }
    @Override
    public void onStop(AppContext appContext) throws Throwable {
        this.doStop = true;
    }
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        this.doLoad = true;
    }
}
