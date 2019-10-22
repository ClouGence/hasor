package net.hasor.tconsole.binder;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.tconsole.TelExecutor;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.util.function.Predicate;
import java.util.function.Supplier;

class InnerConsoleApiBinder extends ApiBinderWrap implements ConsoleApiBinder {
    private InnerExecutorManager executorManager;

    InnerConsoleApiBinder(InnerExecutorManager executorManager, ApiBinder apiBinder) {
        super(apiBinder);
        this.executorManager = executorManager;
    }

    @Override
    public ConsoleApiBinder asHost(Reader reader, Writer writer) {
        this.executorManager.setTelMode(InnerTelMode.Host);
        this.executorManager.setHostReader(reader);
        this.executorManager.setHostWriter(writer);
        return this;
    }

    @Override
    public ConsoleApiBinder setHostSilent() {
        this.executorManager.setHostSilent(true);
        return this;
    }

    @Override
    public ConsoleApiBinder setHostPreCommand(String... commands) {
        this.executorManager.setPreCommandSet(commands);
        return this;
    }

    @Override
    public ConsoleApiBinder asTelnet(InetSocketAddress address, Predicate<String> inBoundMatcher) {
        this.executorManager.setTelMode(InnerTelMode.Telnet);
        this.executorManager.setTelnetSocket(address);
        this.executorManager.setTelnetInBoundMatcher(inBoundMatcher);
        return this;
    }

    @Override
    public CommandBindingBuilder addExecutor(String... names) {
        if (names == null || names.length == 0) {
            throw new NullPointerException("command names undefined.");
        }
        return new CommandBindingBuilderImpl(names);
    }

    private class CommandBindingBuilderImpl implements ConsoleApiBinder.CommandBindingBuilder {
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
