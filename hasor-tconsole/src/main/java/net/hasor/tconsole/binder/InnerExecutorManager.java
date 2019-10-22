package net.hasor.tconsole.binder;
import net.hasor.core.container.AbstractContainer;
import net.hasor.tconsole.TelExecutor;

import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

class InnerExecutorManager extends AbstractContainer {
    private InnerTelMode                                 telMode;
    private Map<String, Supplier<? extends TelExecutor>> telExecutors         = new HashMap<>();
    //
    private InetSocketAddress                            telnetSocket;
    private Predicate<String>                            telnetInBoundMatcher = s -> true;
    private Reader                                       hostReader;
    private Writer                                       hostWriter;
    private boolean                                      hostSilent;
    private String[]                                     preCommandSet;

    public void setTelMode(InnerTelMode telMode) {
        this.telMode = telMode;
    }

    public void setTelnetSocket(InetSocketAddress telnetSocket) {
        this.telnetSocket = telnetSocket;
    }

    public void setTelnetInBoundMatcher(Predicate<String> telnetInBoundMatcher) {
        this.telnetInBoundMatcher = telnetInBoundMatcher;
    }

    public void setHostReader(Reader hostReader) {
        this.hostReader = hostReader;
    }

    public void setHostWriter(Writer hostWriter) {
        this.hostWriter = hostWriter;
    }

    public void setHostSilent(boolean hostSilent) {
        this.hostSilent = hostSilent;
    }

    public void setPreCommandSet(String[] preCommandSet) {
        this.preCommandSet = preCommandSet;
    }

    public void addProvider(String name, Supplier<? extends TelExecutor> provider) {
        this.telExecutors.put(name, provider);
    }

    @Override
    protected void doInitialize() {
        //
    }

    @Override
    protected void doClose() {
        //
    }
}
