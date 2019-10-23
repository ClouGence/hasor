package net.hasor.tconsole.binder;
import net.hasor.core.AppContext;
import net.hasor.core.container.AbstractContainer;
import net.hasor.core.spi.AppContextAware;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.TelContext;
import net.hasor.tconsole.TelExecutor;
import net.hasor.tconsole.launcher.AbstractTelService;
import net.hasor.tconsole.launcher.AttributeObject;
import net.hasor.tconsole.launcher.hosts.HostTelService;
import net.hasor.tconsole.launcher.telnet.TelnetTelService;
import net.hasor.tconsole.spi.TelCloseEventListener;
import net.hasor.tconsole.spi.TelContextListener;
import net.hasor.utils.ExceptionUtils;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.hasor.tconsole.TelOptions.CLOSE_SESSION;

class InnerExecutorManager extends AbstractContainer implements AppContextAware, TelCloseEventListener {
    private InnerTelMode                                 telMode;
    private Map<String, Supplier<? extends TelExecutor>> telExecutors         = new HashMap<>();
    private AttributeObject                              attributeObject      = new AttributeObject();
    private AppContext                                   appContext;
    private AbstractTelService                           service              = null;
    //
    private InetSocketAddress                            telnetSocket;
    private Predicate<String>                            telnetInBoundMatcher = s -> true;
    private boolean                                      hostAnswerExit       = false;
    private Reader                                       hostReader;
    private Writer                                       hostWriter;
    private boolean                                      hostSilent;
    private String[]                                     hostPreCommandSet;

    public void addProvider(String name, Supplier<? extends TelExecutor> provider) {
        this.telExecutors.put(name, provider);
    }

    @Override
    public void onClose(TelCommand trigger, int afterSeconds) {
    }

    @Override
    protected void doInitialize() {
        //
        // .创建服务
        if (InnerTelMode.Host == this.telMode) {
            this.service = new HostTelService(this.hostReader, this.hostWriter, this.appContext);
            if (this.hostSilent) {
                ((HostTelService) this.service).silent();
            }
            for (String key : this.attributeObject.getAttributeNames()) {
                ((HostTelService) this.service).setAttribute(key, this.attributeObject.getAttribute(key));
            }
            // 拦截 Close 命令，如果 hostAnswerExit 配置为 true 。那么遇到 exit 命令就执行它关闭服务。
            this.service.addListener(TelCloseEventListener.class, (trigger, afterSeconds) -> {
                if (!hostAnswerExit) {
                    trigger.getSession().setAttribute(CLOSE_SESSION, "false");
                }
            });
            // 监听容器关闭事件，去同步关闭 AppContext 容器
            this.service.addListener(TelContextListener.class, new TelContextListener() {
                @Override
                public void onStart(TelContext telContext) {
                }

                @Override
                public void onStop(TelContext telContext) {
                    appContext.shutdown();
                }
            });
        }
        if (InnerTelMode.Telnet == this.telMode) {
            this.service = new TelnetTelService(this.telnetSocket, this.telnetInBoundMatcher, this.appContext);
        }
        //
        // .加载命令
        AbstractTelService finalService = Objects.requireNonNull(this.service);
        this.telExecutors.forEach(finalService::addCommand);
        //
        // .启动服务
        this.service.init();
    }

    // .处理 Pre Command
    public void doPreCommand() {
        if (this.service instanceof HostTelService) {
            for (String command : this.hostPreCommandSet) {
                try {
                    ((HostTelService) this.service).sendCommand(command);
                } catch (IOException e) {
                    throw ExceptionUtils.toRuntimeException(e);
                }
            }
        }
    }

    @Override
    protected void doClose() {
        if (this.service.isInit()) {
            this.service.close();
        }
        this.service = null;
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }

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

    public void setHostPreCommandSet(String[] hostPreCommandSet) {
        this.hostPreCommandSet = hostPreCommandSet;
    }

    public void setHostAnswerExit(boolean hostAnswerExit) {
        this.hostAnswerExit = hostAnswerExit;
    }

    public AttributeObject getAttributeObject() {
        return attributeObject;
    }
}