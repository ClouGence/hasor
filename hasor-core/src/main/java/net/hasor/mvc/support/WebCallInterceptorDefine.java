/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.mvc.support;
import net.hasor.core.AppContext;
import net.hasor.core.AppContextAware;
import net.hasor.core.BindInfo;
import net.hasor.core.EventListener;
import net.hasor.mvc.WebCall;
import net.hasor.mvc.WebCallInterceptor;
/**
 * {@link WebCallInterceptor}扩展的定义。
 * @version 2015年6月28日
 * @author 赵永春(zyc@hasor.net)
 */
class WebCallInterceptorDefine implements WebCallInterceptor, EventListener {
    private WebCallInterceptor           callInterceptor;
    private BindInfo<WebCallInterceptor> targetBindInfo;
    //
    public WebCallInterceptorDefine(BindInfo<WebCallInterceptor> targetBindInfo) {
        this.targetBindInfo = targetBindInfo;
    }
    public void onEvent(String event, Object[] params) throws Throwable {
        AppContext appContext = (AppContext) params[0];
        this.callInterceptor = appContext.getInstance(this.targetBindInfo);
        if (this.callInterceptor instanceof AppContextAware) {
            ((AppContextAware) this.callInterceptor).setAppContext(appContext);
        }
    }
    public Object exeCall(Object[] args, WebCall call) throws Throwable {
        if (this.callInterceptor == null) {
            return call.call(args);
        }
        return this.callInterceptor.exeCall(args, call);
    }
}