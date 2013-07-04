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
package org.hasor.binder.support;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.hasor.HasorFramework;
import org.hasor.binder.ErrorHook;
import org.hasor.context.AppContext;
import com.google.inject.Key;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-4-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class ErrorDefinition implements Provider<ErrorDefinition> {
    private Key<? extends ErrorHook>   errorHookKey      = null;
    private ErrorHook                  errorHookInstance = null;
    private Map<String, String>        initParams        = null;
    private Class<? extends Throwable> errorType         = null;
    private AppContext                 appContext        = null;
    //
    //
    public ErrorDefinition(Class<? extends Throwable> errorType, Key<? extends ErrorHook> errorHookKey, Map<String, String> initParams, ErrorHook errorHook) {
        this.errorHookKey = errorHookKey;
        this.initParams = initParams;
        this.errorHookInstance = errorHook;
        this.errorType = errorType;
    }
    //
    private boolean matchesError(Throwable error) {
        return errorType.isAssignableFrom(error.getClass());
    }
    public Map<String, String> getInitParams() {
        return this.initParams;
    }
    @Override
    public ErrorDefinition get() {
        return this;
    }
    protected ErrorHook getTarget(AppContext appContext) {
        if (this.errorHookInstance == null)
            this.errorHookInstance = appContext.getGuice().getInstance(this.errorHookKey);
        return this.errorHookInstance;
    }
    @Override
    public String toString() {
        return HasorFramework.formatString("type %s initParams=%s ,uriPatternType=%s",//
                ErrorDefinition.class, getInitParams(), this.errorType);
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void init(final AppContext appContext) throws ServletException {
        this.appContext = appContext;
        ErrorHook errorHook = this.getTarget(appContext);
        if (errorHook == null)
            return;
        errorHook.init(appContext);
    }
    /**/
    public boolean doError(ServletRequest request, ServletResponse response, Throwable error) throws Throwable {
        boolean serve = this.matchesError(error);
        if (serve == true) {
            ErrorHook hook = this.getTarget(this.appContext);
            if (hook != null)
                hook.doError(request, response, error);
        }
        return serve;
    }
    /**/
    public void destroy(AppContext appContext) {
        ErrorHook errorHook = this.getTarget(this.appContext);
        if (errorHook == null)
            return;
        errorHook.destroy(appContext);
    }
}