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
package org.more.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
/**
 * classpath工具类
 * @version 2010-9-24
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class ClassPathUtil {
    /**扫描classpath时找到资源的回调接口方法。*/
    public interface ScanItem {
        /**
         * 找到资源。
         * @param event 找到资源事件。
         * @param isInJar 找到的资源是否处在jar文件里。
         * @param context 资源所处上下文，对于在jar中的资源该参数表示了所处的哪个jar。对于在目录中的资源该参数表示的就是这个资源文件。
         */
        public boolean goFind(ScanEvent event, boolean isInJar, File context) throws Throwable;
    };
    /*------------------------------------------------------------------------------*/
    private static String[]    CLASS_PATH_PROP    = { "java.class.path", "java.ext.dirs", "sun.boot.class.path" };
    /**ClassPath目录列表。*/
    public static List<File>   CLASS_PATH_Files   = null;
    /**ClassPath目录列表。*/
    public static List<String> CLASS_PATH_Strings = null;
    /*------------------------------------------------------------------------------*/
    static {
        CLASS_PATH_Files = new ArrayList<File>();
        CLASS_PATH_Strings = new ArrayList<String>();
        String delim = ":";
        if (System.getProperty("os.name").indexOf("Windows") != -1)
            delim = ";";
        for (String pro : CLASS_PATH_PROP) {
            String[] pathes = System.getProperty(pro).split(delim);
            for (String path : pathes) {
                CLASS_PATH_Files.add(new File(path));
                CLASS_PATH_Strings.add(path);
            }
        }
    }
    /*------------------------------------------------------------------------------*/
    /**获得classpath中所有JAR文件*/
    public static List<File> getJars() {
        LinkedList<File> jars = new LinkedList<File>();
        for (File classPath : CLASS_PATH_Files)
            if (classPath.isFile() == true)
                jars.add(classPath);
        return jars;
    }
    /**获取classpath中可能存在的资源列表，以流的形式返回。*/
    public static List<InputStream> getResource(String resourcePath) throws IOException {
        LinkedList<InputStream> ins = new LinkedList<InputStream>();
        for (File classPath : CLASS_PATH_Files) {
            InputStream is = null;
            if (classPath.isFile() == true)
                //1.Jar文件
                try {
                    FileInputStream fis = new FileInputStream(classPath);
                    JarInputStream jis = new JarInputStream(fis, false);
                    is = getResourceByZip(jis, resourcePath);
                } catch (Exception e) {}
            else {
                //2.目录
                File res = new File(classPath, resourcePath);
                if (res.canRead() == true)
                    is = new FileInputStream(res);
            }
            if (is != null)
                ins.add(is);
        }
        return ins;
    }
    /**获取zip流中指定的那个资源，该方法并不会从zip流开始初读取，它只会接着流的位置继续读。*/
    public static InputStream getResourceByZip(ZipInputStream zipIN, String resourcePath) throws IOException {
        ZipEntry e = null;
        while ((e = zipIN.getNextEntry()) != null) {
            if (e.getName().equals(resourcePath) == true)
                return zipIN;
            zipIN.closeEntry();
        }
        return null;
    }
    //-----------------------------------------------------------------------------
    /**对某一个目录执行扫描。*/
    private static boolean scanDir(File dirFile, String wild, ScanItem item, File contextDir) throws Throwable {
        String contextPath = contextDir.getAbsolutePath().replace("\\", "/");
        //1.如果进来的就是一个文件。
        if (dirFile.isDirectory() == false) {
            //1)去除上下文目录
            String dirPath = dirFile.getAbsolutePath().replace("\\", "/");
            if (dirPath.startsWith(contextPath) == true)
                dirPath = dirPath.substring(contextPath.length(), dirPath.length());
            //2)计算忽略
            if (StringUtil.matchWild(wild, dirPath) == false)
                return false;
            //3)执行发现
            return item.goFind(new ScanEvent(dirPath, dirFile), false, dirFile);
        }
        //----------
        for (File f : dirFile.listFiles()) {
            //1)去除上下文目录
            String dirPath = f.getAbsolutePath().replace("\\", "/");
            if (dirPath.startsWith(contextPath) == true)
                dirPath = dirPath.substring(contextPath.length(), dirPath.length());
            //3)执行发现
            if (f.isDirectory() == true) {
                //扫描文件夹中的内容
                if (scanDir(f, wild, item, contextDir) == true)
                    return true;
            }
            //2)计算忽略
            if (StringUtil.matchWild(wild, dirPath) == false)
                continue;
            if (item.goFind(new ScanEvent(dirPath, f), false, f) == true)
                return true;
        }
        return false;
    }
    /**对某一个jar文件执行扫描。*/
    public static boolean scanJar(File jarFile, String wild, ScanItem item) throws Throwable {
        FileInputStream fis = new FileInputStream(jarFile);
        JarInputStream jis = new JarInputStream(fis, false);
        JarEntry e = null;
        while ((e = jis.getNextJarEntry()) != null) {
            String name = "/" + e.getName();
            if (StringUtil.matchWild(wild, name) == true)
                if (e.isDirectory() == false)
                    if (item.goFind(new ScanEvent(name, e, jis), true, jarFile) == true) {
                        jis.close();
                        return true;
                    }
        }
        jis.closeEntry();
        jis.close();
        return false;
    }
    /**
     * 扫描classpath目录中的资源，每当发现一个资源时都将产生对{@link ScanItem}接口的一次调用。
     * 如果资源是存在于jar包中的那么在获取的对象输入流时要在回调中处理完毕。
     * 在扫描期间如果想随时退出扫描则{@link ScanItem}接口的返回值给true即可。
     * @param wild 扫描期间要排除资源的通配符。
     * @param item 当找到资源时执行回调的接口。
     */
    public static void scan(String wild, ScanItem item) throws Throwable {
        for (File classPath : CLASS_PATH_Files) {
            boolean res = false;
            if (classPath.isFile() == true)
                res = scanJar(classPath, wild, item);
            else if (classPath.isDirectory() == true)
                res = scanDir(classPath, wild, item, classPath);
            if (res == true)
                return;
        }
    }
}