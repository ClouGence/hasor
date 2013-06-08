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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.more.util.IOUtils;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import org.platform.Platform;
import org.platform.context.AppContext;
import org.platform.servlet.resource.IResourceLoaderCreator;
import org.platform.servlet.resource.ResourceLoader;
import org.platform.servlet.resource.support.ResourceSettings.LoaderConfig;
import org.platform.servlet.resource.util.MimeType;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
/**
 * 负责装载jar包或zip包中的资源
 * @version : 2013-6-5
 * @author 赵永春 (zyc@byshell.org)
 */
public class ResourceLoaderFilter implements Filter {
    @Inject
    private AppContext       appContext = null;
    private MimeType         mimeType   = null;
    private ResourceSettings settings   = null;
    private ResourceLoader[] loaderList = null;
    private File             cacheDir   = null;
    //
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
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
        //4.缓存路径
        this.cacheDir = new File(this.appContext.getWorkSpace().getCacheDir(this.settings.getCacheDir()));
        if (!chekcCacheDir()) {
            Platform.warning("init ResourceLoaderFilter error can not create %s", this.cacheDir);
            int i = 0;
            while (true) {
                this.cacheDir = new File(this.appContext.getWorkSpace().getCacheDir(this.settings.getCacheDir() + "_" + String.valueOf(i)));
                if (chekcCacheDir())
                    break;
            }
        }
        Platform.info("use cacheDir %s", this.cacheDir);
    }
    //
    private boolean chekcCacheDir() {
        this.cacheDir.mkdirs();
        if (this.cacheDir.isDirectory() == false && this.cacheDir.exists() == true)
            return false;
        else
            return true;
    }
    //
    private MimeType getMimeType() throws IOException {
        if (this.mimeType != null)
            return this.mimeType;
        this.mimeType = new MimeType();
        List<URL> listURL = ResourcesUtils.getResources("/META-INF/mime.types.xml");
        if (listURL != null)
            for (URL resourceURL : listURL)
                try {
                    InputStream inStream = ResourcesUtils.getResourceAsStream(resourceURL);
                    this.mimeType.loadStream(inStream, "utf-8");
                } catch (Exception e) {
                    Platform.warning("loadMimeType error at %s", resourceURL);
                }
        return this.mimeType;
    }
    //
    public void forwardTo(File file, ServletRequest request, ServletResponse response) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        String fileExt = requestURI.substring(requestURI.lastIndexOf("."));
        String typeMimeType = req.getServletContext().getMimeType(fileExt);
        if (StringUtils.isBlank(typeMimeType))
            typeMimeType = this.getMimeType().get(fileExt.substring(1).toLowerCase());
        //
        response.setContentType(typeMimeType);
        FileInputStream cacheFile = new FileInputStream(file);
        IOUtils.copy(cacheFile, response.getOutputStream());
        cacheFile.close();
    }
    //
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (this.settings.isEnable() == false) {
            chain.doFilter(request, response);
            return;
        }
        //1.确定时候拦截
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        try {
            requestURI = URLDecoder.decode(requestURI, "utf-8");
        } catch (Exception e) {}
        boolean hit = false;
        for (String s : this.settings.getTypes()) {
            if (requestURI.endsWith("." + s) == true) {
                hit = true;
                break;
            }
        }
        if (hit == false) {
            chain.doFilter(request, response);
            return;
        }
        //2.检查缓存路径中是否存在
        File cacheFile = new File(this.cacheDir, requestURI);
        if (cacheFile.exists()) {
            this.forwardTo(cacheFile, request, response);
            return;
        }
        //3.尝试载入资源
        InputStream inStream = null;
        for (ResourceLoader loader : loaderList) {
            inStream = loader.getResourceAsStream(requestURI);
            if (inStream != null)
                break;
        }
        if (inStream == null) {
            chain.doFilter(request, response);
            return;
        }
        //4.写入临时文件夹
        cacheFile.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(cacheFile);
        IOUtils.copy(inStream, out);
        inStream.close();
        out.flush();
        out.close();
        this.forwardTo(cacheFile, request, response);
    }
    @Override
    public void destroy() {}
}