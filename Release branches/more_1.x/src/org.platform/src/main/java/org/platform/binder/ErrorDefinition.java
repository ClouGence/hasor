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
package org.platform.binder;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.more.global.Global;
import org.more.util.Iterators;
import org.platform.context.AppContext;
import org.platform.context.SettingListener;
import org.platform.context.ViewContext;
import org.platform.context.setting.Config;
import com.google.inject.Injector;
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
    protected ErrorHook getTarget(Injector injector) {
        if (this.errorHookInstance == null)
            this.errorHookInstance = injector.getInstance(this.errorHookKey);
        return this.errorHookInstance;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(ErrorDefinition.class)//
                .append("initParams", getInitParams())//
                .append("uriPatternType", this.errorType)//
                .toString();
    }
    /*--------------------------------------------------------------------------------------------------------*/
    /**/
    public void init(final AppContext appContext) throws ServletException {
        ErrorHook errorHook = this.getTarget(appContext.getGuice());
        if (errorHook == null)
            return;
        final Map<String, String> initParams = this.getInitParams();
        errorHook.init(appContext, new Config() {
            public ServletContext getServletContext() {
                return appContext.getInitContext().getServletContext();
            }
            public String getInitParameter(String s) {
                return initParams.get(s);
            }
            public Enumeration<String> getInitParameterNames() {
                return Iterators.asEnumeration(initParams.keySet().iterator());
            }
            @Override
            public Global getSettings() {
                return appContext.getSettings();
            }
            @Override
            public void addSettingsListener(SettingListener settingsListener) {
                appContext.getInitContext().getConfig().addSettingsListener(settingsListener);
            }
            @Override
            public void removeSettingsListener(SettingListener settingsListener) {
                appContext.getInitContext().getConfig().removeSettingsListener(settingsListener);
            }
        });
    }
    /**/
    public boolean doError(ViewContext viewContext, ServletRequest request, ServletResponse response, Throwable error) throws Throwable {
        boolean serve = this.matchesError(error);
        if (serve == true) {
            ErrorHook hook = this.getTarget(viewContext.getGuice());
            if (hook != null)
                hook.doError(viewContext, request, response, error);
        }
        return serve;
    }
    /**/
    public void destroy(AppContext appContext) {
        ErrorHook errorHook = this.getTarget(appContext.getGuice());
        if (errorHook == null)
            return;
        errorHook.destroy(appContext);
    }
}