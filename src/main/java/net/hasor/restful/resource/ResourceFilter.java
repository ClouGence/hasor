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
package net.hasor.restful.resource;
import net.hasor.core.AppContext;
import org.more.resource.ResourceLoader;
import net.hasor.restful.MimeType;
import net.hasor.web.startup.RuntimeListener;
import org.more.util.StringUtils;
import org.more.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/**
 * 负责装载jar包或zip包中的资源
 *
 * @version : 2013-6-5
 * @author 赵永春 (zyc@hasor.net)
 */
class ResourceFilter implements Filter {
    protected static Logger                                   logger     = LoggerFactory.getLogger(ResourceFilter.class);
    private final    AtomicBoolean                            inited     = new AtomicBoolean(false);
    private          MimeType                                 mimeType   = null;
    private          ResourceLoader[]                         loaderList = null;
    private          String                                   spacePath  = null;
    private          File                                     cacheDir   = null;
    private          ConcurrentHashMap<String, ReadWriteLock> cachingRes = new ConcurrentHashMap<String, ReadWriteLock>();
    private boolean forceRefreshCache;
    public ResourceFilter(File cacheDir) {
        this.cacheDir = cacheDir;
    }
    //
    @Override
    public synchronized void init(FilterConfig config) throws ServletException {
        if (!this.inited.compareAndSet(false, true)) {
            return;
        }
        AppContext appContext = RuntimeListener.getAppContext(config.getServletContext());
        this.forceRefreshCache = appContext.getEnvironment().getSettings().getBoolean("hasor.restful.forceRefreshCache", false);
        this.spacePath = appContext.getEnvironment().getSettings().getString("hasor.restful.resourcePath", "/static");
        //
        List<ResourceLoader> loaderList = appContext.findBindingBean(ResourceLoader.class);
        this.loaderList = loaderList.toArray(new ResourceLoader[loaderList.size()]);
        this.mimeType = appContext.getInstance(MimeType.class);
    }
    //
    //
    //
    /** 响应资源 */
    private void forwardTo(File file, ServletRequest request, ServletResponse response) throws IOException, ServletException {
        if (response.isCommitted()) {
            return;
        }
        HttpServletRequest req = (HttpServletRequest) request;
        String requestURI = req.getRequestURI();
        String fileExt = requestURI.substring(requestURI.lastIndexOf("."));
        String typeMimeType = null;
        if (this.mimeType != null) {
            typeMimeType = this.mimeType.getMimeType(fileExt);
        } else {
            typeMimeType = req.getSession(true).getServletContext().getMimeType(fileExt);
        }
        if (StringUtils.isBlank(typeMimeType)) {
            if (logger.isInfoEnabled()) {
                logger.info(requestURI + " not mapping MimeType!");
            }
        }
        //
        if (typeMimeType != null) {
            response.setContentType(typeMimeType);
        }
        FileInputStream cacheFile = new FileInputStream(file);
        IOUtils.copy(cacheFile, response.getOutputStream());
        cacheFile.close();
    }
    //
    //
    /* 获取 ReadWriteLock 锁 */
    private ReadWriteLock getReadWriteLock(String requestURI) {
        ReadWriteLock newCacheRWLock = new ReentrantReadWriteLock();
        ReadWriteLock cacheRWLock = this.cachingRes.putIfAbsent(requestURI, newCacheRWLock);
        if (cacheRWLock == null) {
            cacheRWLock = newCacheRWLock;
        }
        return cacheRWLock;
    }
    /* 释放 ReadWriteLock 锁 */
    private void releaseReadWriteLock(String requestURI) {
        if (this.cachingRes.containsKey(requestURI)) {
            this.cachingRes.remove(requestURI);
        }
    }
    /** 资源服务入口方法 */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        // 1.确定时候拦截
        String requestURI = request.getRequestURI().substring(request.getContextPath().length());
        if (!requestURI.startsWith(spacePath)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            requestURI = URLDecoder.decode(requestURI, "utf-8");
        } catch (Exception e) {
            logger.warn("URLDecoder.decode error ->" + requestURI);
        }
        // 2.如果为调试模式每次都重新加载资源（不缓存）
        File cacheFile = new File(cacheDir, requestURI);
        if (this.forceRefreshCache) {
            boolean mark = this.cacheRes(cacheFile, requestURI, request, response);
            if (mark) {
                this.forwardTo(cacheFile, request, response);
            } else {
                chain.doFilter(request, response);
            }
            return;
        }
        // 3.检查缓存路径中是否存在，如已存在则直接响应资源。
        boolean forwardType = true;
        if (!cacheFile.exists()) {
            /*-- 下面代码是为了防止缓存穿透 --*/
            //
            // 预先为资源获取一个读写锁，并读锁定。
            ReadWriteLock cacheRWLock = this.getReadWriteLock(requestURI);
            cacheRWLock.readLock().lock();// 读锁定，如果一个写锁存在则程序会等待写锁释放
            if (!cacheFile.exists()) {// 二次判断资源此时可能资源已在得到锁的等待过程中缓存完了。
                /* 升级锁 */
                cacheRWLock.readLock().unlock();
                cacheRWLock.writeLock().lock();
                /* 三次判断，即使成功拿到写锁也可能因为写锁具有多个而造成重复缓存。因此这里要加以判断。 */
                if (!cacheFile.exists()) {
                    forwardType = this.cacheRes(cacheFile, requestURI, request, response);// 当缓存失败时返回false
                }
                /* 降级锁 */
                cacheRWLock.readLock().lock();
                cacheRWLock.writeLock().unlock();
            }
            cacheRWLock.readLock().unlock();// 读取解锁
            // 释放锁资源
            this.releaseReadWriteLock(requestURI);
        }
        // 5.缓存完毕
        if (forwardType) {
            this.forwardTo(cacheFile, request, response);
        } else {
            ((HttpServletResponse) response).sendError(404, "not exist this resource ‘" + requestURI + "’");
        }
    }
    /* 资源缓存 */
    private boolean cacheRes(File cacheFile, String requestURI, ServletRequest request, ServletResponse response) throws IOException, ServletException {
        InputStream inStream = null;
        if (loaderList == null || loaderList.length == 0) {
            return false;
        }
        // 如果debug模式，无论目标是否已经被缓存都重新缓存。
        if (!this.forceRefreshCache && cacheFile.exists()) {
            return true;
        }
        //
        for (ResourceLoader loader : loaderList) {
            if (!loader.exist(requestURI)) {
                continue;
            }
            inStream = loader.getResourceAsStream(requestURI);
            if (inStream != null) {
                break;
            }
        }
        if (inStream == null) {
            return false;
        }
        // 4.写入临时文件夹
        cacheFile.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(cacheFile);
        IOUtils.copy(inStream, out);
        inStream.close();
        out.flush();
        out.close();
        return true;
    }
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}