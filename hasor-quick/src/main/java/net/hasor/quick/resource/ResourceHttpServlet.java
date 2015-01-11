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
package net.hasor.quick.resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.web.startup.RuntimeListener;
import org.more.util.ContextClassLoaderLocal;
import org.more.util.FileUtils;
import org.more.util.IOUtils;
import org.more.util.StringUtils;
/**
 * 负责装载jar包或zip包中的资源
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
public class ResourceHttpServlet extends HttpServlet {
    private static final long                                serialVersionUID = 2470188139577613256L;
    private static ContextClassLoaderLocal<ResourceLoader[]> LoaderList       = new ContextClassLoaderLocal<ResourceLoader[]>();
    private static ContextClassLoaderLocal<File>             CacheDir         = new ContextClassLoaderLocal<File>();
    private Map<String, ReadWriteLock>                       cachingRes       = new HashMap<String, ReadWriteLock>();
    private boolean                                          isDebug;
    //
    public synchronized void init(ServletConfig config) throws ServletException {
        AppContext appContext = RuntimeListener.getLocalAppContext();
        this.isDebug = appContext.getEnvironment().isDebug();
        //
        ResourceLoader[] resLoaderArray = LoaderList.get();
        if (resLoaderArray != null)
            return;
        ResourceLoaderFactory factory = appContext.getInstance(ResourceLoaderFactory.class);
        resLoaderArray = factory.loaderArray(appContext);
        if (resLoaderArray != null && resLoaderArray.length != 0)
            LoaderList.set(resLoaderArray);
    }
    public synchronized static void initCacheDir(File cacheDir) {
        FileUtils.deleteDir(cacheDir);
        cacheDir.mkdirs();
        CacheDir.set(cacheDir);
        Hasor.logInfo("use cacheDir %s", cacheDir);
    }
    //
    //
    //
    //
    /**响应资源*/
    private void forwardTo(File file, ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (response.isCommitted() == true)
            return;
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        String fileExt = requestURI.substring(requestURI.lastIndexOf("."));
        String typeMimeType = req.getSession(true).getServletContext().getMimeType(fileExt);
        if (StringUtils.isBlank(typeMimeType))
            Hasor.logError("%s not mapping MimeType!", requestURI); //typeMimeType = this.getMimeType().get(fileExt.substring(1).toLowerCase());
        //
        if (typeMimeType != null)
            response.setContentType(typeMimeType);
        FileInputStream cacheFile = new FileInputStream(file);
        IOUtils.copy(cacheFile, response.getOutputStream());
        cacheFile.close();
    }
    //
    //
    /*获取 ReadWriteLock 锁*/
    private synchronized ReadWriteLock getReadWriteLock(String requestURI) {
        ReadWriteLock cacheRWLock = null;
        if (this.cachingRes.containsKey(requestURI) == true) {
            cacheRWLock = this.cachingRes.get(requestURI);
        } else {
            cacheRWLock = new ReentrantReadWriteLock();
            this.cachingRes.put(requestURI, cacheRWLock);
        }
        return cacheRWLock;
    }
    /*释放 ReadWriteLock 锁*/
    private synchronized void releaseReadWriteLock(String requestURI) {
        if (this.cachingRes.containsKey(requestURI) == true)
            this.cachingRes.remove(requestURI);
    }
    /**资源服务入口方法*/
    public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        //1.确定时候拦截
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        try {
            requestURI = URLDecoder.decode(requestURI, "utf-8");
        } catch (Exception e) {}
        //2.检查缓存路径中是否存在
        File cacheFile = new File(CacheDir.get(), requestURI);
        if (this.isDebug) {
            boolean mark = this.cacheRes(cacheFile, requestURI, request, response);
            if (mark)
                this.forwardTo(cacheFile, request, response);
            else
                ((HttpServletResponse) response).sendError(404, "not exist :" + requestURI);
            return;
        }
        //3.创建锁-A
        ReadWriteLock cacheRWLock = this.getReadWriteLock(requestURI);
        //4.在锁下缓存文件，下面代码是为了防止缓存穿透
        boolean forwardType = true;
        cacheRWLock.readLock().lock();//读取锁定
        if (!cacheFile.exists()) {
            /*升级锁*/
            cacheRWLock.readLock().unlock();
            cacheRWLock.writeLock().lock();
            /*在debug模式下，或者尚未缓存时*/
            if (this.isDebug || !cacheFile.exists()) {
                forwardType = this.cacheRes(cacheFile, requestURI, request, response);//当缓存失败时返回false
            }
            cacheRWLock.readLock().lock();
            cacheRWLock.writeLock().unlock();
        }
        cacheRWLock.readLock().unlock();//读取解锁
        //5.缓存完毕
        if (forwardType)
            this.forwardTo(cacheFile, request, response);
        else
            ((HttpServletResponse) response).sendError(404, "not exist this resource ‘" + requestURI + "’");
        //6.释放锁-A
        this.releaseReadWriteLock(requestURI);
    }
    /*资源缓存*/
    private boolean cacheRes(File cacheFile, String requestURI, ServletRequest request, ServletResponse response) throws IOException, ServletException {
        //3.尝试载入资源 
        ResourceLoader inLoader = null;
        InputStream inStream = null;
        ResourceLoader[] loaderList = LoaderList.get();
        if (loaderList == null || loaderList.length == 0)
            return false;
        //
        for (ResourceLoader loader : loaderList) {
            if (loader.exist(requestURI) == false)
                continue;
            if (this.isDebug) {
                if (!loader.canModify(requestURI) && cacheFile.exists())
                    return true;
            }
            //
            inLoader = loader;
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
        inLoader.close(inStream);
        out.flush();
        out.close();
        return true;
    }
}