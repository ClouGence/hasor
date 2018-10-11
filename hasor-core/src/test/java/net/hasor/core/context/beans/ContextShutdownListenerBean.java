package net.hasor.core.context.beans;
import net.hasor.core.AppContext;
import net.hasor.core.context.ContextShutdownListener;
//
public class ContextShutdownListenerBean implements ContextShutdownListener {
    private int i = 0;
    //
    public int getI() {
        return i;
    }
    //
    @Override
    public void doShutdown(AppContext appContext) {
        i++;
    }
    @Override
    public void doShutdownCompleted(AppContext appContext) {
        i++;
    }
}
