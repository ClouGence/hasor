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
import net.hasor.utils.StringUtils;
import net.hasor.utils.future.BasicFuture;
import net.hasor.web.*;
import net.hasor.web.binder.FilterDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * 负责解析参数并执行调用。
 * @version : 2014年8月27日
 * @author 赵永春 (zyc@hasor.net)
 */
class InvokerCaller extends InvokerCallerParamsBuilder implements ExceuteCaller {
    protected static Logger            logger          = LoggerFactory.getLogger(InvokerCaller.class);
    private          FilterDef[]       filterArrays    = null;
    private          Supplier<Invoker> invokerSupplier = null;

    public InvokerCaller(Supplier<Invoker> invokerSupplier, FilterDef[] filterArrays) {
        this.invokerSupplier = invokerSupplier;
        this.filterArrays = (filterArrays == null) ? new FilterDef[0] : filterArrays;
    }

    /** 调用目标 */
    public Future<Object> invoke(final FilterChain chain) {
        Invoker invoker = this.invokerSupplier.get();
        Mapping ownerMapping = invoker.ownerMapping();
        HttpServletRequest httpRequest = invoker.getHttpRequest();
        Method targetMethod = ownerMapping.findMethod(httpRequest);
        //
        // .异步调用
        final BasicFuture<Object> future = new BasicFuture<>();
        boolean needAsync = ownerMapping.isAsync(httpRequest);
        ServletVersion version = invoker.getAppContext().getInstance(ServletVersion.class);
        if (version.ge(ServletVersion.V3_0) && needAsync) {
            // .必须满足: Servlet3.x、环境支持异步Servlet、目标开启了Servlet3
            AsyncContext asyncContext = httpRequest.startAsync(httpRequest, invoker.getHttpResponse());
            asyncContext.start(new AsyncInvocationWorker(asyncContext, targetMethod) {
                public void doWork(Method targetMethod) throws Throwable {
                    future.completed(invoke(targetMethod, invoker));
                }

                @Override
                public void doWorkWhenError(Method targetMethod, Throwable e) {
                    future.failed(e);
                }
            });
            return future;
        }
        //
        // .同步调用
        try {
            Object invoke = invoke(targetMethod, invoker);
            future.completed(invoke);
        } catch (Throwable e) {
            future.failed(e);
        }
        return future;
    }

    /** 执行调用 */
    private Object invoke(final Method targetMethod, Invoker invoker) throws Throwable {
        //
        // .初始化 Controller
        final Object targetObject = invoker.getAppContext().getInstance(invoker.ownerMapping().getTargetType());
        if (targetObject instanceof Controller) {
            ((Controller) targetObject).initController(invoker);
        }
        if (targetObject == null) {
            throw new NullPointerException("mappingToDefine newInstance is null.");
        }
        //
        // .准备过滤器链
        final InvokerChain invokerChain = inv -> {
            // 设置contentType
            String contentType = inv.contentType();
            if (StringUtils.isNotBlank(contentType)) {
                if (!inv.getHttpResponse().isCommitted()) {
                    inv.getHttpResponse().setContentType(contentType);
                }
            }
            // 执行调用
            try {
                final Object[] resolveParamsArrays = this.resolveParams(inv, targetMethod);
                Object result = targetMethod.invoke(targetObject, resolveParamsArrays);
                inv.put(Invoker.RETURN_DATA_KEY, result);
                return result;
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        };
        //
        // .执行Filters
        return new InvokerChainInvocation(this.filterArrays, invokerChain).doNext(invoker);
    }
}
