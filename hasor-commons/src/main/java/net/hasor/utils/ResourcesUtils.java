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
import net.hasor.utils.io.input.AutoCloseInputStream;

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
                return new AutoCloseInputStream(new FileInputStream(this.file));
            }
            return null;
        }
    }

    /** 扫描classpath时找到资源的回调接口方法。*/
    public static interface Scanner {
        /**
         * 找到资源(返回值为true表示找到预期的资源结束扫描，false表示继续扫描剩下的资源)
         * @param event 找到资源事件。
         * @param isInJar 找到的资源是否处在jar文件里。
         */
        public void found(ScanEvent event, boolean isInJar) throws IOException;
    }

    /*------------------------------------------------------------------------------*/
    public static String formatResource(String resourcePath) {
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

    /** 合成所有属性文件的配置信息到一个{@link Map}接口中。*/
    public static Map<String, String> getProperty(final String[] resourcePaths) throws IOException {
        return getProperty(Arrays.asList(resourcePaths).iterator());
    }

    /** 合成所有属性文件的配置信息到一个{@link Map}接口中。*/
    public static Map<String, String> getProperty(final Iterator<String> iterator) throws IOException {
        if (iterator == null) {
            return null;
        }
        //
        ClassLoader classLoader = getCurrentLoader();
        Map<String, String> fullData = new HashMap<>();
        while (iterator.hasNext()) {
            String str = iterator.next();
            Map<String, String> att = getProperty(classLoader, str);
            fullData.putAll(att);
        }
        return fullData;
    }

    /** 读取一个属性文件，并且以{@link Map}接口的形式返回。*/
    public static Map<String, String> getProperty(final String resourcePath) throws IOException {
        return getProperty(getCurrentLoader(), resourcePath);
    }

    /** 读取一个属性文件，并且以{@link Map}接口的形式返回。*/
    public static Map<String, String> getProperty(final ClassLoader classLoader, final String resourcePath) throws IOException {
        Properties prop = new Properties();
        InputStream in = getResourceAsStream(classLoader, resourcePath);
        if (in != null) {
            prop.load(in);
        }
        HashMap<String, String> resultData = new HashMap<>();
        for (Object keyObj : prop.keySet()) {
            String key = (String) keyObj;
            String val = prop.getProperty(key);
            resultData.put(key, val);
        }
        return resultData;
    }

    /*------------------------------------------------------------------------------*/

    /** 获取 classpath 中可能存在的资源。*/
    public static URL getResource(String resourcePath) throws IOException {
        if (StringUtils.isBlank(resourcePath)) {
            return null;
        }
        //
        if (resourcePath.startsWith("classpath:")) {
            resourcePath = resourcePath.substring("classpath:".length());
            return getResource(getCurrentLoader(), resourcePath);
        } else if (resourcePath.startsWith("http:") || resourcePath.startsWith("https:") || resourcePath.startsWith("file:") || resourcePath.startsWith("jar:") || resourcePath.startsWith("ftp:")) {
            return new URL(resourcePath);
        } else {
            return getResource(getCurrentLoader(), resourcePath);
        }
    }

    /** 获取 classpath 中可能存在的资源。*/
    public static URL getResource(ClassLoader classLoader, String resourcePath) throws IOException {
        resourcePath = formatResource(resourcePath);
        return classLoader.getResource(resourcePath);
    }

    /** 获取 classpath 中可能存在的资源列表。*/
    public static List<URL> getResources(String resourcePath) throws IOException {
        return getResources(getCurrentLoader(), resourcePath);
    }

    /** 获取 classpath 中可能存在的资源列表。*/
    public static List<URL> getResources(ClassLoader classLoader, String resourcePath) throws IOException {
        if (resourcePath == null) {
            return new ArrayList<>(0);
        }
        //
        resourcePath = formatResource(resourcePath);
        ArrayList<URL> urls = new ArrayList<>();
        Enumeration<URL> urlEnumeration = classLoader.getResources(resourcePath);
        while (urlEnumeration.hasMoreElements()) {
            URL url = urlEnumeration.nextElement();
            urls.add(url);
        }
        return urls;
    }

    /*------------------------------------------------------------------------------*/

    /** 获取可能存在的资源，以流的形式返回。*/
    public static InputStream getResourceAsStream(File resourceFile) throws IOException {
        return getResourceAsStream(getCurrentLoader(), resourceFile.toURI().toURL());
    }

    /** 获取classpath中可能存在的资源，以流的形式返回。*/
    public static InputStream getResourceAsStream(URI resourceURI) throws IOException {
        return getResourceAsStream(getCurrentLoader(), resourceURI.toURL());
    }

    /** 获取classpath中可能存在的资源，以流的形式返回。*/
    public static InputStream getResourceAsStream(String resourcePath) throws IOException {
        URL resource = getResource(resourcePath);
        if (resource == null) {
            return null;
        }
        return getResourceAsStream(getCurrentLoader(), resource);
    }

    /** 获取classpath中可能存在的资源，以流的形式返回。*/
    public static InputStream getResourceAsStream(URL resourceURL) throws IOException {
        return getResourceAsStream(getCurrentLoader(), resourceURL);
    }

    /** 获取classpath中可能存在的资源，以流的形式返回。*/
    public static InputStream getResourceAsStream(ClassLoader classLoader, URI resourceURI) throws IOException {
        return getResourceAsStream(classLoader, resourceURI.toURL());
    }

    /**获取classpath中可能存在的资源，以流的形式返回。*/
    public static InputStream getResourceAsStream(ClassLoader classLoader, String resourcePath) throws IOException {
        URL resource = getResource(resourcePath);
        if (resource == null) {
            return null;
        }
        return getResourceAsStream(classLoader, resource);
    }

    /** 获取classpath中可能存在的资源，以流的形式返回。*/
    public static InputStream getResourceAsStream(ClassLoader classLoader, URL resourceURL) throws IOException {
        String protocol = resourceURL.getProtocol().trim().toLowerCase();
        switch (protocol) {
            case "classpath": {
                String resourcePath = resourceURL.getPath();
                return getResourceAsStream(classLoader, resourcePath);
            }
            case "http":
            case "https":
            case "ftp": {
                return new AutoCloseInputStream(resourceURL.openStream());
            }
            case "file": {
                File targetFile = new File(resourceURL.getPath());
                if (targetFile.exists()) {
                    if (targetFile.canRead() && targetFile.isFile()) {
                        return new AutoCloseInputStream(new FileInputStream(targetFile));
                    } else {
                        throw new IOException("resource " + targetFile.getAbsolutePath() + " can not be read.");
                    }
                }
                return null;
            }
            case "jar": {
                //JAR文件
                JarFile jar = ((JarURLConnection) resourceURL.openConnection()).getJarFile();
                String jarFile = jar.getName().replace("\\", "/");
                String resourcePath = URLDecoder.decode(resourceURL.getPath(), "utf-8");
                int beginIndex = resourcePath.indexOf(jarFile) + jarFile.length();
                String entPath = resourcePath.substring(beginIndex + 2);
                ZipEntry e = jar.getEntry(entPath);
                return new AutoCloseInputStream(jar.getInputStream(e));
            }
            default:
                return classLoader.getResourceAsStream(resourceURL.toString());
        }
    }

    /** 获取classpath中可能存在的资源列表，以流的形式返回。*/
    public static List<InputStream> getResourceAsStreamList(String resourcePath) throws IOException {
        return getResourceAsStreamList(getCurrentLoader(), resourcePath);
    }

    /** 获取classpath中可能存在的资源列表，以流的形式返回。*/
    public static List<InputStream> getResourceAsStreamList(ClassLoader classLoader, String resourcePath) throws IOException {
        ArrayList<InputStream> iss = new ArrayList<>();
        List<URL> urls = getResources(classLoader, resourcePath);
        for (URL url : urls) {
            InputStream in = getResourceAsStream(classLoader, url);
            if (in != null) {
                iss.add(new AutoCloseInputStream(in));
            }
        }
        return iss;
    }

    /**
     * Loads a class
     * @param className - the class to fetch
     * @return The loaded class
     * @throws ClassNotFoundException If the class cannot be found (duh!)
     */
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return getCurrentLoader().loadClass(className);
    }
    /*------------------------------------------------------------------------------*/

    /** 对某一个目录执行扫描。*/
    private static void scanDir(final File dirFile, final String wild, final Scanner item, final File contextDir) throws IOException {
        String contextPath = contextDir.getAbsolutePath().replace("\\", "/");
        //1.如果进来的就是一个文件。
        if (!dirFile.isDirectory()) {
            //1)去除上下文目录
            String dirPath = dirFile.getAbsolutePath().replace("\\", "/");
            if (dirPath.startsWith(contextPath)) {
                dirPath = dirPath.substring(contextPath.length());
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
                dirPath = dirPath.substring(contextPath.length() + 1);
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

    /** 对某一个jar文件执行扫描。*/
    public static void scanJar(final JarFile jarFile, final String wild, final Scanner item) throws IOException {
        final Enumeration<JarEntry> jes = jarFile.entries();
        while (jes.hasMoreElements()) {
            JarEntry e = jes.nextElement();
            String name = e.getName();
            if (MatchUtils.matchWild(wild, name)) {
                if (!e.isDirectory()) {
                    try (InputStream jarFileInputStream = jarFile.getInputStream(e)) {
                        item.found(new ScanEvent(name, e, jarFileInputStream), true);
                    }
                }
            }
        }
    }

    /**
     * 扫描classpath目录中的资源，每当发现一个资源时都将产生对{@link Scanner}接口的一次调用。请注意首个字符不可以是通配符。
     * 如果资源是存在于jar包中的那么在获取的对象输入流时要在回调中处理完毕。
     * 在扫描期间如果想随时退出扫描则{@link Scanner}接口的返回值给true即可。
     * @param wild 扫描期间要排除资源的通配符。
     * @param item 当找到资源时执行回调的接口。
     */
    public static void scan(final String wild, final Scanner item) throws IOException, URISyntaxException {
        scan(getCurrentLoader(), wild, item);
    }

    /**
     * 扫描classpath目录中的资源，每当发现一个资源时都将产生对{@link Scanner}接口的一次调用。请注意首个字符不可以是通配符。
     * 如果资源是存在于jar包中的那么在获取的对象输入流时要在回调中处理完毕。
     * 在扫描期间如果想随时退出扫描则{@link Scanner}接口的返回值给true即可。
     * @param wild 扫描期间要排除资源的通配符。
     * @param item 当找到资源时执行回调的接口。
     */
    public static void scan(ClassLoader classLoader, final String wild, final Scanner item) throws IOException, URISyntaxException {
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
        index1 = index1 == -1 ? wild.length() : index1;
        index2 = index2 == -1 ? wild.length() : index2;
        int index = Math.min(index1, index2);
        //
        String _wild = wild.substring(0, index);
        if (_wild.charAt(_wild.length() - 1) == '/') {
            _wild = _wild.substring(0, _wild.length() - 1);
        }
        Enumeration<URL> urls = findAllClassPath(classLoader, _wild);
        List<URL> dirs = rootDir(classLoader);
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

    private static URL has(final List<URL> dirs, final URL one) {
        for (URL u : dirs) {
            if (one.toString().startsWith(u.toString())) {
                return u;
            }
        }
        return null;
    }

    private static List<URL> rootDir(ClassLoader classLoader) throws IOException {
        Enumeration<URL> roote = findAllClassPath(classLoader, "");
        ArrayList<URL> rootList = new ArrayList<>();
        while (roote.hasMoreElements()) {
            rootList.add(roote.nextElement());
        }
        return rootList;
    }

    /** 获取所有ClassPath条目 */
    public static Enumeration<URL> findAllClassPath(ClassLoader classLoader, final String name) throws IOException {
        return classLoader.getResources(name);
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
