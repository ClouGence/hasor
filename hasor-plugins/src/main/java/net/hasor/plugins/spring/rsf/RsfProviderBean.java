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
package net.hasor.plugins.spring.rsf;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfPublisher;
/**
 * 服务提供者
 * @version : 2016-11-08
 * @author 赵永春 (zyc@hasor.net)
 */
public class RsfProviderBean extends AbstractRsfBean implements Provider<Object> {
    private Object target;
    private boolean sharedThreadPool = true;
    public Object getTarget() {
        return target;
    }
    public void setTarget(Object target) {
        this.target = target;
    }
    public boolean isSharedThreadPool() {
        return this.sharedThreadPool;
    }
    public void setSharedThreadPool(boolean sharedThreadPool) {
        this.sharedThreadPool = sharedThreadPool;
    }
    //
    @Override
    public Object getObject() throws Exception {
        return this.getTarget();
    }
    @Override
    public Object get() {
        return this.getTarget();
    }
    @Override
    protected RsfPublisher.RegisterBuilder<?> configService(RsfPublisher.RegisterBuilder<?> builder) {
        if (!this.isSharedThreadPool()) {
            builder = builder.asAloneThreadPool();
        }
        return builder;
    }
}