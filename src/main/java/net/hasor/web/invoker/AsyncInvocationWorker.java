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
package net.hasor.web.invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import java.lang.reflect.Method;
/**
 * Servlet 3 异步请求处理
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AsyncInvocationWorker implements Runnable {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private AsyncContext asyncContext;
    private Method       targetMethod;
    //
    public AsyncInvocationWorker(AsyncContext asyncContext, Method targetMethod) {
        this.asyncContext = asyncContext;
        this.targetMethod = targetMethod;
    }
    @Override
    public void run() {
        try {
            this.doWork(this.targetMethod);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        } finally {
            this.asyncContext.complete();
        }
    }
    public abstract void doWork(Method targetMethod) throws Throwable;
}