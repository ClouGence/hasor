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
package net.hasor.utils;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
/**
 * 资源加载工具类，所有方法均是程序级优先。
 * @version 2010-9-24
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class ResourcesUtils {
    /** 发现事件 */
    public static class ScanEvent {
        private String      name    = null;
        private boolean     isRead  = false; //是否可读。
        private boolean     isWrite = false; //是否可写
        //
        private InputStream stream  = null;
        private File        file    = null;
        //
        /**创建{@link ScanEvent}*/
        ScanEvent(final String name, final File file) {
            this.isRead = file.canRead();
            this.isWrite = file.canWrite();
            this.file = file;
            this.name = name;
        }
        /**创建{@link ScanEvent}*/
        ScanEvent(final String name, final JarEntry entry, final InputStream stream) {
            this.isRead = !entry.isDirectory();
            this.isWrite = false;
            this.stream = stream;
            this.name = name;
        }
        //----------------------------------
        public String getName() {
            return this.name;
        }
        public boolean isRead() {
            return this.isRead;
        }
        public boolean isWrite() {
            return this.isWrite;
        }
        public InputStream getStream() throws FileNotFoundException {
            if (this.stream != null) {
                return this.stream;
            }
            if (this.file != null && this.isRead) {
                return new FileInputStream(this.file);
            }
            return null;
        }
    }
    /**扫描classpath时找到资源的回调接口方法。*/
    public static interface ScanItem {
        /**
         * 找到资源(返回值为true表示找到预期的资源结束扫描，false表示继续扫描剩下的资源)
         * @param event 找到资源事件。
         * @param isInJar 找到的资源是否处在jar文件里。
         */
        public void found(ScanEvent event, boolean isInJar) throws IOException;
    }
    ;
    /*------------------------------------------------------------------------------*/
    private static String formatResource(String resourcePath) {
        if (resourcePath != null && resourcePath.length() > 1) {
            if (resourcePath.charAt(0) == '/') {
                resourcePath = resourcePath.substring(1);
            }
        }
        return resourcePath;
    }
    private static ClassLoader getCurrentLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
    /**合成所有属性文件的配置信息到一个{@link Map}接口中。*/
    public static Map<String, String> getPropertys(final String[] resourcePaths) throws IOException {
        return getPropertys(Arrays.asList(resourcePaths).iterator());
    }
    /**合成所有属性文件的配置信息到一个{@link Map}接口中。*/
    public static Map<String, String> getPropertys(final Iterator<String> iterator) throws IOException {
        if (iterator == null) {
            return null;
        }
        //
        Map<String, String> fullData = new HashMap<String, String>();
        while (iterator.hasNext()) {
            String str = iterator.next();
            Map<String, String> att = getPropertys(str);
            if (att != null) {
                fullData.putAll(att);
            }
        }
        return fullData;
    }
    /**读取一个属性文件，并且以{@link Map}接口的形式返回。*/
    public static Map<String, String> getPropertys(final String resourcePath) throws IOException {
        Properties prop = new Properties();
        InputStream in = getResourceAsStream(formatResource(resourcePath));
        if (in != null) {
            prop.load(in);
        }
        HashMap<String, String> resultData = new HashMap<String, String>();
        for (Object keyObj : prop.keySet()) {
            String key = (String) keyObj;
            String val = prop.getProperty(key);
            resultData.put(key, val);
        }
        return resultData;
    }
    /**获取classpath中可能存在的资源。*/
    public static URL getResource(String resourcePath) throws IOException {
        if (resourcePath == null) {
            return null;
        }
        resourcePath = formatResource(resourcePath);
        URL url = getCurrentLoader().getResource(resourcePath);
        return url;
    }
    /**获取classpath中可能存在的资源列表。*/
    public static List<URL> getResources(String resourcePath) throws IOException {
        if (resourcePath == null) {
            return new ArrayList<URL>(0);
        }
        //
        resourcePath = formatResource(resourcePath);
        ArrayList<URL> urls = new ArrayList<URL>();
        Enumeration<URL> eurls = getCurrentLoader().getResources(resourcePath);
        while (eurls.hasMoreElements()) {
            URL url = eurls.nextElement();
            urls.add(url);
        }
        return urls;
    }
    /**获取可能存在的资源，以流的形式返回。*/
    public static InputStream getResourceAsStream(final File resourceFile) throws IOException {
        return getResourceAsStream(resourceFile.toURI().toURL());
    }
    /**获取classpath中可能存在的资源，以流的形式返回。*/
    public static InputStream getResourceAsStream(final URI resourceURI) throws IOException {
        return getResourceAsStream(resourceURI.toURL());
    }
    /**获取classpath中可能存在的资源，以流的形式返回。*/
    public static InputStream getResourceAsStream(final URL resourceURL) throws IOException {
        String protocol = resourceURL.getProtocol();
        File path = new File(URLDecoder.decode(resourceURL.getFile(), "utf-8"));
        if (protocol.equals("file")) {
            //文件
            if (path.canRead() && path.isFile()) {
                return new AutoCloseInputStream(new FileInputStream(path));
            }
        } else if (protocol.equals("jar")) {
            //JAR文件
            JarFile jar = ((JarURLConnection) resourceURL.openConnection()).getJarFile();
            String jarFile = jar.getName().replace("\\", "/");
            String resourcePath = URLDecoder.decode(resourceURL.getPath(), "utf-8");
            int beginIndex = resourcePath.indexOf(jarFile) + jarFile.length();
            String entPath = resourcePath.substring(beginIndex + 2);
            ZipEntry e = jar.getEntry(entPath);
            return jar.getInputStream(e);
        } else if (protocol.equals("classpath")) {
            String resourcePath = formatResource(resourceURL.getPath());
            return getResourceAsStream(resourcePath);
        }
        // TODO 该处处理其他协议的资源加载。诸如OSGi等协议。
        return null;
    }
    /**获取classpath中可能存在的资源，以流的形式返回。*/
    public static InputStream getResourceAsStream(String resourcePath) throws IOException {
        resourcePath = formatResource(resourcePath);
        InputStream inStream = getCurrentLoader().getResourceAsStream(resourcePath);
        if (inStream == null)
            inStream = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
        return inStream;
    }
    /**获取classpath中可能存在的资源列表，以流的形式返回。*/
    public static List<InputStream> getResourcesAsStream(final String resourcePath) throws IOException {
        ArrayList<InputStream> iss = new ArrayList<InputStream>();
        List<URL> urls = getResources(resourcePath);//已经调用过，formatResource(resourcePath);
        for (URL url : urls) {
            InputStream in = getResourceAsStream(url);//已经调用过，formatResource(resourcePath);
            if (in != null) {
                iss.add(in);
            }
        }
        return iss;
    }
    /*------------------------------------------------------------------------------*/
    /**对某一个目录执行扫描。*/
    private static void scanDir(final File dirFile, final String wild, final ScanItem item, final File contextDir) throws IOException {
        String contextPath = contextDir.getAbsolutePath().replace("\\", "/");
        //1.如果进来的就是一个文件。
        if (!dirFile.isDirectory()) {
            //1)去除上下文目录
            String dirPath = dirFile.getAbsolutePath().replace("\\", "/");
            if (dirPath.startsWith(contextPath)) {
                dirPath = dirPath.substring(contextPath.length(), dirPath.length());
            }
            //2)计算忽略
            if (!MatchUtils.matchWild(wild, dirPath)) {
                return;
            }
            //3)执行发现
            item.found(new ScanEvent(dirPath, dirFile), false);
            return;
        }
        //----------
        File[] files = dirFile.listFiles();
        files = (files == null) ? new File[0] : files;
        for (File f : files) {
            //1)去除上下文目录
            String dirPath = f.getAbsolutePath().replace("\\", "/");
            if (dirPath.startsWith(contextPath)) {
                dirPath = dirPath.substring(contextPath.length() + 1, dirPath.length());
            }
            //3)执行发现
            if (f.isDirectory()) {
                //扫描文件夹中的内容
                scanDir(f, wild, item, contextDir);
            }
            //2)计算忽略
            if (!MatchUtils.matchWild(wild, dirPath)) {
                continue;
            }
            item.found(new ScanEvent(dirPath, f), false);
        }
    }
    /**对某一个jar文件执行扫描。*/
    public static void scanJar(final JarFile jarFile, final String wild, final ScanItem item) throws IOException {
        final Enumeration<JarEntry> jes = jarFile.entries();
        while (jes.hasMoreElements()) {
            JarEntry e = jes.nextElement();
            String name = e.getName();
            if (MatchUtils.matchWild(wild, name)) {
                if (!e.isDirectory()) {
                    item.found(new ScanEvent(name, e, jarFile.getInputStream(e)), true);
                }
            }
        }
    }
    /**
     * 扫描classpath目录中的资源，每当发现一个资源时都将产生对{@link ScanItem}接口的一次调用。请注意首个字符不可以是通配符。
     * 如果资源是存在于jar包中的那么在获取的对象输入流时要在回调中处理完毕。
     * 在扫描期间如果想随时退出扫描则{@link ScanItem}接口的返回值给true即可。
     * @param wild 扫描期间要排除资源的通配符。
     * @param item 当找到资源时执行回调的接口。
     */
    public static void scan(final String wild, final ScanItem item) throws IOException, URISyntaxException {
        if (wild == null || wild.equals("")) {
            return;
        }
        char firstChar = wild.charAt(0);
        if (firstChar == '?' || firstChar == '*') {
            throw new IllegalArgumentException("classpath包扫描不支持首个字母为通配符字符。");
        }
        //确定位置
        int index1 = wild.indexOf('?');
        int index2 = wild.indexOf('*');
        index1 = index1 == -1 ? index1 = wild.length() : index1;
        index2 = index2 == -1 ? index2 = wild.length() : index2;
        int index = index1 > index2 ? index2 : index1;
        //
        String _wild = wild.substring(0, index);
        if (_wild.charAt(_wild.length() - 1) == '/') {
            _wild = _wild.substring(0, _wild.length() - 1);
        }
        Enumeration<URL> urls = findAllClassPath(_wild);
        List<URL> dirs = rootDir();
        //
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            String protocol = url.getProtocol();
            if (protocol.equals("file")) {
                File f = new File(url.toURI());
                scanDir(f, wild, item, new File(has(dirs, url).toURI()));
            } else if (protocol.equals("jar")) {
                JarURLConnection urlc = (JarURLConnection) url.openConnection();
                scanJar(urlc.getJarFile(), wild, item);
            }
        }
    }
    ;
    private static URL has(final List<URL> dirs, final URL one) {
        for (URL u : dirs) {
            if (one.toString().startsWith(u.toString())) {
                return u;
            }
        }
        return null;
    }
    private static List<URL> rootDir() throws IOException {
        Enumeration<URL> roote = findAllClassPath("");
        ArrayList<URL> rootList = new ArrayList<URL>();
        while (roote.hasMoreElements()) {
            rootList.add(roote.nextElement());
        }
        return rootList;
    }
    ;
    /**获取所有ClassPath条目*/
    public static Enumeration<URL> findAllClassPath(final String name) throws IOException {
        ClassLoader loader = getCurrentLoader();
        return loader.getResources(name);
        //        Enumeration<URL> urls = null;
        //        if (loader instanceof URLClassLoader == false)
        //            urls = loader.getResources(name);
        //        else {
        //            URLClassLoader urlLoader = (URLClassLoader) loader;
        //            /*
        //             * Jetty 使用getResources、Tomcat 使用findResources
        //             * 在Jetty中WebappsClassLoader只实现了没有重写findResources
        //             * 在Tomcat中WebappsClassLoader只实现了没有重写getResources
        //             * 
        //             * TODO : 该处逻辑为：首先判断findResources方法是否被重写，如果被重写则调用它否则调用getResources
        //             */
        //            try {
        //                Class<?> loaderType = urlLoader.getClass();
        //                Method m = loaderType.getMethod("findResources", String.class);
        //                if (m.getDeclaringClass() == loaderType)
        //                    urls = urlLoader.findResources(name);
        //                else
        //                    urls = urlLoader.getResources(name);
        //            } catch (Exception e) {
        //                urls = urlLoader.findResources(name);//Default
        //            }
        //        }
        //        return urls;
    }
}