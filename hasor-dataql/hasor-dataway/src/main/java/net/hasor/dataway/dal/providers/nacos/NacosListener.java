package net.hasor.dataway.dal.providers.nacos;
import com.alibaba.nacos.api.config.listener.Listener;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

public abstract class NacosListener implements Listener {
    private ScheduledExecutorService executorService;

    public NacosListener(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public Executor getExecutor() {
        return this.executorService;
    }
}
