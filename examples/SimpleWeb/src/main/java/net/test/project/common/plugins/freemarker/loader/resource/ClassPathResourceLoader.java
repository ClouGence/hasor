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
package net.test.project.common.plugins.freemarker.loader.resource;
import java.io.InputStream;
import java.net.URL;
import net.test.project.common.plugins.freemarker.loader.IResourceLoader;
/**
 * {@link ClassPathResourceLoader}会使用一个classpath路径作为相对路径。
 * 假设该类代表了一个名为“org.more.res”的资源包，
 * {@link #getResourceAsStream(String)}方法参数为“/abc/aa/htm”。
 * 那么这个资源的实际地址是位于classpath下的“org/more/res/abc/aa/htm”。
 * @version : 2011-9-14
 * @author 赵永春 (zyc@byshell.org) 
 */
public class ClassPathResourceLoader implements IResourceLoader {
    private String      packageName = null;
    private ClassLoader classLoader = null;
    /***/
    public ClassPathResourceLoader(String packageName, ClassLoader classLoader) {
        this.packageName = packageName;
        this.classLoader = classLoader;
    }
    /**获取资源获取的包路径。*/
    public String getPackageName() {
        return this.packageName;
    }
    /**获取装载资源使用的类装载器。*/
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
    public URL getResource(String name) {
        String $name = this.packageName + "/" + name;
        if ($name.charAt(0) == '/')
            $name = $name.substring(1);
        $name = $name.replaceAll("/{2}", "/");
        return this.classLoader.getResource($name);
    }
    public InputStream getResourceAsStream(String name) {
        String $name = this.packageName + "/" + name;
        if ($name.charAt(0) == '/')
            $name = $name.substring(1);
        $name = $name.replaceAll("/{2}", "/");
        return this.classLoader.getResourceAsStream($name);
    }
}