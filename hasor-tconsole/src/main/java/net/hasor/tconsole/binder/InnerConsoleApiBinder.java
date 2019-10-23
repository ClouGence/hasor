package net.hasor.tconsole.binder;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.tconsole.TelExecutor;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

class InnerConsoleApiBinder extends ApiBinderWrap implements ConsoleApiBinder {
    private InnerExecutorManager executorManager;

    InnerConsoleApiBinder(InnerExecutorManager executorManager, ApiBinder apiBinder) {
        super(apiBinder);
        this.executorManager = executorManager;
    }

    @Override
    public HostBuilder asHost(Reader reader, Writer writer) {
        this.executorManager.setTelMode(InnerTelMode.Host);
        this.executorManager.setHostReader(reader);
        this.executorManager.setHostWriter(writer);
        return new TelnetBuilderImpl();
    }

    @Override
    public TelnetBuilder asTelnet(InetSocketAddress address, Predicate<String> inBoundMatcher) {
        this.executorManager.setTelMode(InnerTelMode.Telnet);
        this.executorManager.setTelnetSocket(address);
        this.executorManager.setTelnetInBoundMatcher(inBoundMatcher);
        return new TelnetBuilderImpl();
    }

    private class TelnetBuilderImpl implements HostBuilder, TelnetBuilder {
        @Override
        public HostBuilder silent() {
            executorManager.setHostSilent(true);
            return this;
        }

        @Override
        public HostBuilder preCommand(String... commands) {
            executorManager.setHostPreCommandSet(commands);
            return this;
        }

        @Override
        public HostBuilder answerExit() {
            executorManager.setHostAnswerExit(true);
            return this;
        }

        @Override
        public CommandBindingBuilder addExecutor(String... names) {
            if (names == null || names.length == 0) {
                throw new NullPointerException("command names undefined.");
            }
            return new CommandBindingBuilderImpl(names);
        }

        @Override
        public Object getAttribute(String key) {
            return executorManager.getAttributeObject().getAttribute(key);
        }

        @Override
        public void setAttribute(String key, Object value) {
            executorManager.getAttributeObject().setAttribute(key, value);
        }

        @Override
        public Set<String> getAttributeNames() {
            return executorManager.getAttributeObject().getAttributeNames();
        }
    }

    private class CommandBindingBuilderImpl implements CommandBindingBuilder {
        private String[] names;

        CommandBindingBuilderImpl(String[] names) {
            this.names = names;
        }

        @Override
        public <T extends TelExecutor> void to(Class<? extends T> executorKey) {
            toInfo(bindType(TelExecutor.class).to(executorKey).toInfo());
        }

        @Override
        public <T extends TelExecutor> void toConstructor(Constructor<T> constructor) {
            toInfo(bindType(TelExecutor.class).toConstructor(constructor).toInfo());
        }

        @Override
        public <T extends TelExecutor> void toInfo(BindInfo<? extends T> executorInfo) {
            toProvider(getProvider(executorInfo));
        }

        @Override
        public <T extends TelExecutor> void toInstance(T instance) {
            toProvider(() -> instance);
        }

        @Override
        public <T extends TelExecutor> void toProvider(Supplier<? extends T> executorProvider) {
            for (String name : this.names) {
                executorManager.addProvider(name, executorProvider);
            }
        }
    }
}
