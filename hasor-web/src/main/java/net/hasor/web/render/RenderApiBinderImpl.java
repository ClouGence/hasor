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
package net.hasor.web.render;
import net.hasor.core.*;
import net.hasor.core.binder.ApiBinderWrap;
import net.hasor.utils.ExceptionUtils;
import net.hasor.utils.StringUtils;
import net.hasor.web.RenderEngine;
import net.hasor.web.WebApiBinder;
import net.hasor.web.annotation.Render;
import net.hasor.web.invoker.InMappingDef;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.lang.reflect.Method;
import java.util.*;
/**
 * @version : 2017-01-08
 * @author 赵永春 (zyc@hasor.net)
 */
public class RenderApiBinderImpl extends ApiBinderWrap {
    private BindInfo<DefaultServlet> defaultServletBindInfo;
    protected RenderApiBinderImpl(ApiBinder apiBinder) {
        super(apiBinder);
        this.defaultServletBindInfo = apiBinder.bindType(DefaultServlet.class).toInfo();
    }
    //
    public WebApiBinder.RenderEngineBindingBuilder<RenderEngine> suffix(String suffix, String... moreSuffix) {
        return new RenderEngineBindingBuilderImpl(newArrayList(moreSuffix, suffix));
    }
    public WebApiBinder.RenderEngineBindingBuilder<RenderEngine> suffix(String[] suffixArrays) {
        return new RenderEngineBindingBuilderImpl(newArrayList(suffixArrays, null));
    }
    private class RenderEngineBindingBuilderImpl implements WebApiBinder.RenderEngineBindingBuilder<RenderEngine> {
        private List<String> suffixList = new ArrayList<String>();
        private boolean      enable     = false;
        public RenderEngineBindingBuilderImpl(List<String> suffixList) {
            Set<String> suffixSet = new LinkedHashSet<String>();
            for (String str : suffixList) {
                if (StringUtils.isNotBlank(str)) {
                    suffixSet.add(str.toUpperCase());
                }
            }
            this.suffixList = new ArrayList<String>(suffixSet);
            this.enable = !this.suffixList.isEmpty();
        }
        @Override
        public void bind(Class<? extends RenderEngine> engineType) {
            if (this.enable)
                bindSuffix(this.suffixList, bindType(RenderEngine.class).uniqueName().to(engineType).toInfo());
        }
        @Override
        public void bind(RenderEngine engine) {
            if (this.enable)
                bindSuffix(this.suffixList, bindType(RenderEngine.class).uniqueName().toInstance(engine).toInfo());
        }
        @Override
        public void bind(Provider<? extends RenderEngine> engineProvider) {
            if (this.enable)
                bindSuffix(this.suffixList, bindType(RenderEngine.class).uniqueName().toProvider(engineProvider).toInfo());
        }
        @Override
        public void bind(BindInfo<? extends RenderEngine> engineRegister) {
            if (this.enable)
                bindSuffix(this.suffixList, Hasor.assertIsNotNull(engineRegister));
        }
    }
    private void bindSuffix(List<String> suffixList, BindInfo<? extends RenderEngine> bindInfo) {
        suffixList = Collections.unmodifiableList(suffixList);
        this.bindType(RenderDefinition.class).toInstance(new RenderDefinition(suffixList, bindInfo)).toInfo();
        //
        Method serviceMethod = null;
        try {
            serviceMethod = DefaultServlet.class.getMethod("service", new Class[] { ServletRequest.class, ServletResponse.class });
        } catch (NoSuchMethodException e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
        for (String suffix : suffixList) {
            if (suffix.equals("*"))
                continue;
            if (StringUtils.isBlank(suffix))
                continue;
            //
            String pattern = "/*." + suffix.toLowerCase();
            InMappingDef define = new InMappingDef(Long.MAX_VALUE, this.defaultServletBindInfo, pattern, Arrays.asList(serviceMethod), false);
            bindType(InMappingDef.class).uniqueName().toInstance(define);/*单例*/
        }
    }
    //
    public void scanAnnoRender(Matcher<Class<? extends RenderEngine>> matcher, String... packages) {
        String[] defaultPackages = this.getEnvironment().getSpanPackage();
        String[] scanPackages = (packages == null || packages.length == 0) ? defaultPackages : packages;
        //
        Set<Class<?>> renderSet = this.findClass(RenderEngine.class, scanPackages);
        for (Class<?> renderClass : renderSet) {
            if (renderClass.isInterface())
                continue;
            if (renderClass == RenderEngine.class)
                continue;
            //
            Class<? extends RenderEngine> target = (Class<? extends RenderEngine>) renderClass;
            if (matcher != null && !matcher.matches(target))
                continue;
            //
            Render renderInfo = renderClass.getAnnotation(Render.class);
            if (renderInfo != null && renderInfo.value().length > 0) {
                String[] renderName = renderInfo.value();
                suffix(renderName).bind(target);
            }
        }
        //
    }
    //
    // ------------------------------------------------------------------------------------------------------
    protected static List<String> newArrayList(final String[] arr, final String object) {
        ArrayList<String> list = new ArrayList<String>();
        if (arr != null) {
            Collections.addAll(list, arr);
        }
        if (object != null) {
            list.add(object);
        }
        return list;
    }
}