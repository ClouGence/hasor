/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package org.hasor.servlet.binder.support;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.hasor.context.AppContext;
import org.hasor.servlet.WebErrorHook;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-4-11
 * @author ’‘”¿¥∫ (zyc@hasor.net)
 */
class ErrorDefinition implements Provider<ErrorDefinition> {
    private Key<? extends WebErrorHook> errorHookKey      = null;
    private WebErrorHook                errorHookInstance = null;
    private Map<String, String>         initParams        = null;
    private Class<? extends Throwable>  errorType         = null;
    private AppContext                  appContext        = null;
    //
    //
    public ErrorDefinition(Class<? extends Throwable> errorType, Key<? extends WebErrorHook> errorHookKey, Map<String, String> initParams, WebErrorHook webErrorHook) {
        this.errorHookKey = errorHookKey;
        this.initParams = initParams;
        this.errorHookInstance = webErrorHook;
        this.errorType = errorType;
    }
    //
    private boolean matchesError(Throwable error) {
        return errorType.isAssignableFrom(error.getClass());
    }
    public Map<String, String> getInitParams() {
        return this.initParams;
    }
    public ErrorDefinition get() {
        return this;
    }
    protected WebErrorHook getTarget(AppContext appContext) {
        if (this.errorHookInstance == null)
            this.errorHookInstance = appContext.getGuice().getInstance(this.errorHookKey);
        return this.errorHookInstance;
    }
    public String toString() {
        return String.format("type %s initParams=%s ,uriPatternType=%s",//
                ErrorDefinition.class, getInitParams(), this.errorType);
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void init(final AppContext appContext) throws ServletException {
        this.appContext = appContext;
        WebErrorHook webErrorHook = this.getTarget(appContext);
        if (webErrorHook == null)
            return;
        //webErrorHook.init(appContext);
    }
    /**/
    public boolean doError(ServletRequest request, ServletResponse response, Throwable error) throws Throwable {
        boolean serve = this.matchesError(error);
        if (serve == true) {
            WebErrorHook hook = this.getTarget(this.appContext);
            if (hook != null)
                hook.doError(request, response, error);
        }
        return serve;
    }
    /**/
    public void destroy(AppContext appContext) {
        WebErrorHook webErrorHook = this.getTarget(this.appContext);
        if (webErrorHook == null)
            return;
        //webErrorHook.destroy(appContext);
    }
}