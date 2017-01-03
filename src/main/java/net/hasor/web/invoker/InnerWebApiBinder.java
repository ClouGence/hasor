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
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Hasor;
import net.hasor.core.Provider;
import net.hasor.core.provider.InfoAwareProvider;
import net.hasor.web.*;
import net.hasor.web.pipeline.PipelineWebApiBinder;

import java.util.Map;
/**
 * 该类是{@link WebApiBinder}接口实现。
 * @version : 2013-4-10
 * @author 赵永春 (zyc@hasor.net)
 */
class InnerWebApiBinder extends PipelineWebApiBinder implements WebApiBinder {
    private ServletVersion curVersion;
    private MimeType       mimeType;
    InnerWebApiBinder(ServletVersion curVersion, MimeType mimeType, ApiBinder apiBinder) {
        super(apiBinder);
        this.curVersion = Hasor.assertIsNotNull(curVersion);
        this.mimeType = Hasor.assertIsNotNull(mimeType);
    }
    @Override
    public String getMimeType(String suffix) {
        return this.mimeType.getMimeType(suffix);
    }
    @Override
    public ServletVersion getServletVersion() {
        return this.curVersion;
    }
    //
    @Override
    public WebApiBinder addPlugin(Class<? extends WebPlugin> webPlugin) {
        webPlugin = Hasor.assertIsNotNull(webPlugin);
        BindInfo<WebPlugin> bindInfo = this.bindType(WebPlugin.class).to(webPlugin).toInfo();
        this.bindType(WebPluginDefinition.class).toInstance(new WebPluginDefinition(bindInfo));
        return this;
    }
    @Override
    public WebApiBinder addPlugin(WebPlugin webPlugin) {
        webPlugin = Hasor.assertIsNotNull(webPlugin);
        BindInfo<WebPlugin> bindInfo = this.bindType(WebPlugin.class).toInstance(webPlugin).toInfo();
        this.bindType(WebPluginDefinition.class).toInstance(new WebPluginDefinition(bindInfo));
        return this;
    }
    @Override
    public WebApiBinder addPlugin(Provider<? extends WebPlugin> webPlugin) {
        webPlugin = Hasor.assertIsNotNull(webPlugin);
        BindInfo<WebPlugin> bindInfo = this.bindType(WebPlugin.class).toProvider(webPlugin).toInfo();
        this.bindType(WebPluginDefinition.class).toInstance(new WebPluginDefinition(bindInfo));
        return this;
    }
    @Override
    public WebApiBinder addPlugin(BindInfo<? extends WebPlugin> webPlugin) {
        webPlugin = Hasor.assertIsNotNull(webPlugin);
        this.bindType(WebPluginDefinition.class).toInstance(new WebPluginDefinition(webPlugin));
        return this;
    }
    @Override
    public WebApiBinder addSetup(Class<? extends MappingSetup> setup) {
        this.bindType(MappingSetup.class).to(Hasor.assertIsNotNull(setup));
        return this;
    }
    @Override
    public WebApiBinder addSetup(MappingSetup setup) {
        this.bindType(MappingSetup.class).toInstance(Hasor.assertIsNotNull(setup));
        return this;
    }
    @Override
    public WebApiBinder addSetup(Provider<? extends MappingSetup> setup) {
        this.bindType(MappingSetup.class).toProvider(Hasor.assertIsNotNull(setup));
        return this;
    }
    @Override
    public WebApiBinder addSetup(BindInfo<? extends MappingSetup> setup) {
        InfoAwareProvider<MappingSetup> provider = new InfoAwareProvider<MappingSetup>(Hasor.assertIsNotNull(setup));
        this.bindType(MappingSetup.class).toProvider(Hasor.autoAware(this.getEnvironment(), provider));
        return this;
    }
    @Override
    protected void throughInvFilter(int index, String pattern, UriPatternMatcher matcher, BindInfo<? extends InvokerFilter> filterRegister, Map<String, String> initParams) {
        InvokeFilterDefinition define = new InvokeFilterDefinition(index, pattern, matcher, filterRegister, initParams);
        bindType(InvokeFilterDefinition.class).uniqueName().toInstance(define);
    }
}