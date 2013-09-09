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
package net.hasor.mvc.resource.support;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import net.hasor.Hasor;
import net.hasor.core.AppContext;
import net.hasor.mvc.resource.ResourceLoader;
import net.hasor.mvc.resource.ResourceLoaderCreator;
import net.hasor.mvc.resource.support.ResourceSettings.LoaderConfig;
import net.hasor.mvc.resource.util.MimeType;
import org.more.util.IOUtils;
import org.more.util.ResourcesUtils;
import org.more.util.StringUtils;
import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
/**
 * 负责装载jar包或zip包中的资源
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class ResourceLoaderFilter implements Filter {
    @Inject
    private AppContext                 appContext     = null;
    private MimeType                   mimeType       = null;
    private ResourceSettings           settings       = null;
    private ResourceLoader[]           loaderList     = null;
    private File                       cacheDir       = null;
    private Map<String, ReadWriteLock> cachingRes     = new HashMap<String, ReadWriteLock>();
    private Lock                       cachingResLock = new ReentrantLock();
    //
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
        List<ResourceLoader> resourceLoaderList = new ArrayList<ResourceLoader>();
        for (LoaderConfig item : this.settings.getLoaders()) {
            String loaderType = item.loaderType;
            String configVal = item.config.getText();
            ResourceLoaderCreator creator = null;
            //从已经注册的TemplateLoader中获取一个TemplateLoaderCreator进行构建。
            for (ResourceLoaderCreatorDefinition define : loaderCreatorList)
                if (StringUtils.equalsIgnoreCase(define.getName(), loaderType)) {
                    creator = define.get();
                    break;
                }
            if (creator == null) {
                Hasor.warning("missing %s ResourceLoaderCreator!", loaderType);
            } else {
                try {
                    ResourceLoader loader = creator.newInstance(this.appContext, item.config);
                    if (loader == null)
                        Hasor.error("%s ResourceLoaderCreator call newInstance return is null. config is %s.", loaderType, configVal);
                    else {
                        resourceLoaderList.add(loader);
                    }
                } catch (Exception e) {
                    Hasor.error("%s ResourceLoaderCreator has error.%s", loaderType, e);
                }
            }
            //
        }
        //3.保存
        this.loaderList = resourceLoaderList.toArray(new ResourceLoader[resourceLoaderList.size()]);
        //4.缓存路径
        String cacheHome = this.appContext.getEnvironment().evalEnvVar("HTTP_CACHE_PATH");
        //
        this.cacheDir = new File(cacheHome);
        if (!chekcCacheDir()) {
            Hasor.warning("init ResourceLoaderFilter error can not create %s", this.cacheDir);
            int i = 0;
            while (true) {
                this.cacheDir = new File(cacheHome + "_" + String.valueOf(i));
                if (chekcCacheDir())
                    break;
            }
        }
        //5.
        Hasor.info("use cacheDir %s", this.cacheDir);
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
                    Hasor.warning("loadMimeType error at %s", resourceURL);
                }
        return this.mimeType;
    }
    //
    public void forwardTo(File file, ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (response.isCommitted() == true)
            return;
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        String fileExt = requestURI.substring(requestURI.lastIndexOf("."));
        String typeMimeType = req.getSession(true).getServletContext().getMimeType(fileExt);
        if (StringUtils.isBlank(typeMimeType))
            typeMimeType = this.getMimeType().get(fileExt.substring(1).toLowerCase());
        //
        response.setContentType(typeMimeType);
        FileInputStream cacheFile = new FileInputStream(file);
        IOUtils.copy(cacheFile, response.getOutputStream());
        cacheFile.close();
    }
    //
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //1.确定时候拦截
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        try {
            requestURI = URLDecoder.decode(requestURI, "utf-8");
        } catch (Exception e) {}
        boolean hit = false;
        for (String s : this.settings.getContentTypes()) {
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
        //3.创建锁
        ReadWriteLock cacheRWLock = null;
        this.cachingResLock.lock();
        if (this.cachingRes.containsKey(requestURI) == true) {
            cacheRWLock = this.cachingRes.get(requestURI);
        } else {
            cacheRWLock = new ReentrantReadWriteLock();
            this.cachingRes.put(requestURI, cacheRWLock);
        }
        this.cachingResLock.unlock();
        //4.在锁下缓存文件
        boolean forwardType = true;
        cacheRWLock.readLock().lock();//读取锁定
        if (!cacheFile.exists()) {
            /*升级锁*/
            cacheRWLock.readLock().unlock();
            cacheRWLock.writeLock().lock();
            if (!cacheFile.exists()) {
                forwardType = this.cacheRes(cacheFile, requestURI, request, response, chain);//当缓存失败时返回false
            }
            cacheRWLock.readLock().lock();
            cacheRWLock.writeLock().unlock();
        }
        cacheRWLock.readLock().unlock();//读取解锁
        //
        if (forwardType)
            this.forwardTo(cacheFile, request, response);
        else
            chain.doFilter(request, response);
        //
        this.cachingRes.remove(requestURI);
    }
    private boolean cacheRes(File cacheFile, String requestURI, ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //3.尝试载入资源 
        InputStream inStream = null;
        for (ResourceLoader loader : loaderList) {
            inStream = loader.getResourceAsStream(requestURI);
            if (inStream != null)
                break;
        }
        if (inStream == null)
            return false;
        //4.写入临时文件夹
        cacheFile.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(cacheFile);
        IOUtils.copy(inStream, out);
        inStream.close();
        out.flush();
        out.close();
        return true;
    }
    public void destroy() {}
}