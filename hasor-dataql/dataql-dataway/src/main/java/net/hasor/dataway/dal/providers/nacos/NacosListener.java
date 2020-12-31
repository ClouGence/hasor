/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.dataway.dal.providers.nacos;
import com.alibaba.nacos.api.config.listener.Listener;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Nacos 监听器基类。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-09-21
 */
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
