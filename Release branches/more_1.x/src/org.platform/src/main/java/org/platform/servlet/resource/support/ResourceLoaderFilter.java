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
package org.platform.servlet.resource.support;
import java.io.IOException;
import java.util.ArrayList;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.more.util.StringUtils;
import org.platform.Platform;
import org.platform.context.AppContext;
import org.platform.servlet.resource.IResourceLoaderCreator;
import org.platform.servlet.resource.ResourceLoader;
import org.platform.servlet.resource.support.ResourceSettings.LoaderConfig;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
/**
 * 负责装载jar包或zip包中的资源
 * @version : 2013-6-5
 * @author 赵永春 (zyc@byshell.org)
 */
@Singleton
public class ResourceLoaderFilter implements Filter {
    @Inject
    private AppContext       appContext = null;
    private ResourceSettings settings   = null;
    private ResourceLoader[] loaderList = null;
    private boolean          init       = false;
    //
    //
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (init == true)
            return;
        init = true;
        this.settings = appContext.getInstance(ResourceSettings.class);
        //1.载入所有loaderCreator
        ArrayList<ResourceLoaderCreatorDefinition> loaderCreatorList = new ArrayList<ResourceLoaderCreatorDefinition>();
        TypeLiteral<ResourceLoaderCreatorDefinition> LOADER_DEFS = TypeLiteral.get(ResourceLoaderCreatorDefinition.class);
        for (Binding<ResourceLoaderCreatorDefinition> entry : this.appContext.getGuice().findBindingsByType(LOADER_DEFS)) {
            ResourceLoaderCreatorDefinition define = entry.getProvider().get();
            define.setAppContext(this.appContext);
            loaderCreatorList.add(define);
        }
        //2.配置loader
        ArrayList<ResourceLoader> resourceLoaderList = new ArrayList<ResourceLoader>();
        for (LoaderConfig item : this.settings.getLoaders()) {
            String loaderType = item.loaderType;
            String configVal = item.config.getText();
            IResourceLoaderCreator creator = null;
            //从已经注册的TemplateLoader中获取一个TemplateLoaderCreator进行构建。
            for (ResourceLoaderCreatorDefinition define : loaderCreatorList)
                if (StringUtils.eqUnCaseSensitive(define.getName(), loaderType)) {
                    creator = define.get();
                    break;
                }
            if (creator == null) {
                Platform.warning("missing %s ResourceLoaderCreator!", loaderType);
            } else {
                try {
                    ResourceLoader loader = creator.newInstance(this.appContext, item.config);
                    if (loader == null)
                        Platform.error("%s ResourceLoaderCreator call newInstance return is null. config is %s.", loaderType, configVal);
                    else {
                        String logInfo = configVal.replace("\n", "");
                        logInfo = logInfo.length() > 30 ? logInfo.substring(0, 30) : logInfo;
                        Platform.info("[%s] ResourceLoader is added. config = %s", loaderType, logInfo);
                        resourceLoaderList.add(loader);
                    }
                } catch (Exception e) {
                    Platform.error("%s ResourceLoaderCreator has error.%s", e);
                }
            }
            //
        }
        //3.保存
        this.loaderList = resourceLoaderList.toArray(new ResourceLoader[resourceLoaderList.size()]);
    }
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        boolean hit = false;
        for (String s : this.settings.getTypes()) {
            if (s.startsWith("*.") == false)
                hit = StringUtils.matchWild("*." + s, requestURI);
            else
                hit = StringUtils.matchWild(s, requestURI);
            if (hit)
                break;
        }
        if (hit == false) {
            chain.doFilter(request, response);
            return;
        }
        System.out.println();
    }
    @Override
    public void destroy() {}
}