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
package org.more.classcode;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 
 * @version : 2014年9月7日
 * @author 赵永春(zyc@hasor.net)
 */
public class MoreClassLoader extends ClassLoader {
    private Map<String, AbstractClassConfig> classMap = new ConcurrentHashMap<String, AbstractClassConfig>();
    //
    public MoreClassLoader() {
        //
    }
    public MoreClassLoader(ClassLoader parentLoader) {
        super(parentLoader);
    }
    //
    public AbstractClassConfig findClassConfig(String className) {
        return this.classMap.get(className);
    }
    //
    protected final Class<?> findClass(final String className) throws ClassNotFoundException {
        if (this.classMap.containsKey(className) == true) {
            byte[] bs = this.classMap.get(className).getBytes();
            return this.defineClass(className, bs, 0, bs.length);
        }
        return super.findClass(className);
    }
    public InputStream getResourceAsStream(final String classResource) {
        if (classResource.endsWith(".class")) {
            String className = classResource.substring(0, classResource.length() - 6).replace("/", ".");
            if (this.classMap.containsKey(className) == true) {
                AbstractClassConfig ce = this.classMap.get(className);
                return new ByteArrayInputStream(ce.getBytes());
            }
        }
        return super.getResourceAsStream(classResource);
    }
    /***/
    public void addClassConfig(AbstractClassConfig config) {
        String cname = config.getClassName();
        if (this.classMap.containsKey(cname) == false) {
            this.classMap.put(cname, config);
        }
    }
}