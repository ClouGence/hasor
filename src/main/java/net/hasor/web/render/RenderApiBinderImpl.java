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
import net.hasor.web.definition.ServletDefinition;
import net.hasor.web.definition.UriPatternMatcher;
import net.hasor.web.definition.UriPatternType;
import org.more.util.StringUtils;

import javax.servlet.http.HttpServlet;
import java.util.*;
/**
 * 该类是{@link RenderApiBinder}接口实现。
 * @version : 2017-01-08
 * @author 赵永春 (zyc@hasor.net)
 */
public class RenderApiBinderImpl extends ApiBinderWrap implements RenderApiBinder {
    protected RenderApiBinderImpl(ApiBinder apiBinder) {
        super(apiBinder);
    }
    @Override
    public RenderEngineBindingBuilder<RenderEngine> suffix(String suffix, String... moreSuffix) {
        return new RenderEngineBindingBuilderImpl(newArrayList(moreSuffix, suffix));
    }
    @Override
    public RenderEngineBindingBuilder<RenderEngine> suffix(String[] suffixArrays) {
        return new RenderEngineBindingBuilderImpl(newArrayList(suffixArrays, null));
    }
    private class RenderEngineBindingBuilderImpl implements RenderEngineBindingBuilder<RenderEngine> {
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
        this.bindType(RenderDefinition.class).toInstance(new RenderDefinition(suffixList, bindInfo));
        //
        for (String suffix : suffixList) {
            if (suffix.equals("*"))
                continue;
            if (StringUtils.isBlank(suffix))
                continue;
            //
            String pattern = "*." + suffix.toLowerCase();
            BindInfo<HttpServlet> servletInfo = bindType(HttpServlet.class).uniqueName().toInstance(new DefaultRenderHttpServlet()).toInfo();
            UriPatternMatcher matcher = UriPatternType.get(UriPatternType.SERVLET, pattern);
            ServletDefinition define = new ServletDefinition(Long.MAX_VALUE, pattern, matcher, servletInfo, null);
            bindType(ServletDefinition.class).uniqueName().toInstance(define);/*单例*/
        }
    }
    //
    @Override
    public void scanAnnoRender() {
        this.scanAnnoRender(new Matcher<Class<? extends RenderEngine>>() {
            @Override
            public boolean matches(Class<? extends RenderEngine> target) {
                return true;
            }
        });
    }
    @Override
    public void scanAnnoRender(String... packages) {
        this.scanAnnoRender(new Matcher<Class<? extends RenderEngine>>() {
            @Override
            public boolean matches(Class<? extends RenderEngine> target) {
                return true;
            }
        });
    }
    @Override
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