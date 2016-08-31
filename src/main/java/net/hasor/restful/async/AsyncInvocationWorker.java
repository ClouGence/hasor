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
package net.hasor.restful.async;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * Servlet 3 异步请求处理
 * @version : 2013-4-13
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AsyncInvocationWorker implements Runnable {
    private HttpServletRequest  request;
    private HttpServletResponse response;
    private AsyncContext        asyncContext;
    public AsyncInvocationWorker(AsyncContext asyncContext, HttpServletRequest request, HttpServletResponse response) {
        this.asyncContext = asyncContext;
        this.request = request;
        this.response = response;
    }
    @Override
    public void run() {
        try {
            this.doWork(this.request, this.response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.asyncContext.complete();
        }
    }
    public abstract void doWork(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
}