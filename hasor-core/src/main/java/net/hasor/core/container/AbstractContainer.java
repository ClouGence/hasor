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
package net.hasor.core.container;
import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Container 的公共类，提供了 doInitialize、doClose 两个方法的支持
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2015年11月25日
 */
public abstract class AbstractContainer implements Closeable {
    private AtomicBoolean inited = null;

    public AbstractContainer() {
        this.inited = new AtomicBoolean(false);
    }

    /** 执行初始化 */
    public final boolean isInit() {
        return this.inited.get();
    }

    /** 执行容器初始化，当 {@link #isInit} 方法返回 false 的时候会触发 {@link #doInitialize()} 调用。 */
    public final void init() {
        if (!this.inited.compareAndSet(false, true)) {
            throw new IllegalStateException("the Container has been inited.");
        }
        this.doInitialize();
    }

    /** 执行容器关闭，当 {@link #isInit} 方法返回 true 的时候会触发 {@link #doClose()} 调用。 */
    @Override
    public final void close() {
        if (!this.inited.compareAndSet(true, false)) {
            throw new IllegalStateException("the Container has been closed.");
        }
        this.doClose();
    }

    protected abstract void doInitialize();

    protected abstract void doClose();
}