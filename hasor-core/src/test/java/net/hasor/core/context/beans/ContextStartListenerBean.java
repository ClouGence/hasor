package net.hasor.core.context.beans;
import net.hasor.core.AppContext;
import net.hasor.core.context.ContextStartListener;
//
public class ContextStartListenerBean implements ContextStartListener {
    private int i = 0;
    //
    public int getI() {
        return i;
    }
    //
    @Override
    public void doStart(AppContext appContext) {
        i++;
    }
    @Override
    public void doStartCompleted(AppContext appContext) {
        i++;
    }
}