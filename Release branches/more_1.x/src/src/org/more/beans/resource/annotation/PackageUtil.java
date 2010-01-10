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
package org.more.beans.resource.annotation;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
/**
 * classpath扫描工具。
 * @version 2010-1-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class PackageUtil {
    private static String[]   CLASS_PATH_PROP  = { "java.class.path", "java.ext.dirs", "sun.boot.class.path" };
    private static List<File> CLASS_PATH_ARRAY = getClassPath();
    private static List<File> getClassPath() {
        List<File> ret = new ArrayList<File>();
        String delim = ":";
        if (System.getProperty("os.name").indexOf("Windows") != -1)
            delim = ";";
        for (String pro : CLASS_PATH_PROP) {
            String[] pathes = System.getProperty(pro).split(delim);
            for (String path : pathes)
                ret.add(new File(path));
        }
        return ret;
    }
    /**对某一个目录执行扫描。*/
    private LinkedList<String> scanDir(File dirFile, String prefix, PackageUtilExclude exclude) {
        LinkedList<String> classNames = new LinkedList<String>();
        for (File f : dirFile.listFiles()) {
            String pn = prefix + File.pathSeparator + f.getName();
            if (f.exists() == false || exclude.exclude(pn, f.isFile()) == true)
                continue;
            if (f.isFile())
                classNames.add(pn);
            else
                classNames.addAll(scanDir(f, pn, exclude));
        }
        return classNames;
    }
    /**对某一个jar文件执行扫描。*/
    public LinkedList<String> scanJar(File jarFile, PackageUtilExclude exclude) throws IOException {
        LinkedList<String> classNames = new LinkedList<String>();
        FileInputStream fis = new FileInputStream(jarFile);
        JarInputStream jis = new JarInputStream(fis, false);
        JarEntry e = null;
        while ((e = jis.getNextJarEntry()) != null) {
            String eName = e.getName();
            if (exclude.exclude(eName, true) == false)
                classNames.addLast(eName);
            jis.closeEntry();
        }
        jis.close();
        return classNames;
    }
    /**扫描classpath*/
    public LinkedList<String> scanClassPath(PackageUtilExclude exclude) throws IOException {
        LinkedList<String> ret = new LinkedList<String>();
        for (File classPath : CLASS_PATH_ARRAY)
            if (classPath.isFile() == true)
                ret.addAll(scanJar(classPath, exclude));
            else if (classPath.isDirectory() == true)
                ret.addAll(scanDir(classPath, "", exclude));
        return ret;
    }
}