package net.hasor.core.context.beans;
import net.hasor.core.*;
//
public class ContextInjectBean {
    @Inject
    private AppContext   appContext;
    @Inject
    private EventContext eventContext;
    @Inject
    private Environment  environment;
    @Inject
    private Settings     settings;
    //
    public AppContext getAppContext() {
        return appContext;
    }
    public EventContext getEventContext() {
        return eventContext;
    }
    public Environment getEnvironment() {
        return environment;
    }
    public Settings getSettings() {
        return settings;
    }
}