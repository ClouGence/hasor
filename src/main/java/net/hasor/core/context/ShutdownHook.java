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
package net.hasor.core.context;
import net.hasor.core.Hasor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 用于处理当虚拟机关闭时{@link TemplateAppContext}的shutdown过程。
 * @version : 2015年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
class ShutdownHook extends Thread implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private TemplateAppContext appContext;
    public ShutdownHook(TemplateAppContext appContext) {
        this.appContext = Hasor.assertIsNotNull(appContext);
    }
    public void run() {
        ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader newLoader = this.appContext.getClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(newLoader);
            this.setName("Hasor-ShutdownHook");
            this.appContext.shutdown();
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldLoader);
        }
    }
}