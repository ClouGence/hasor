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
package org.more.global;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.util.ResourcesUtil;
/**
 * {@link Global}创建工厂
 * @version : 2011-9-29
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class GlobalFactory {
    /**默认编码*/
    public final static String DefaultEncoding = "utf-8";
    /**创建{@link Global}对象*/
    protected abstract Map<String, Object> loadConfig(InputStream stream, String encoding) throws IOException;
    /*------------------------------------------------------------------------*/
    /**解析资源并且生成Global可以使用的Map。*/
    public Map<String, Object> createMap(String encoding, Object[] objects) throws IOException {
        //1.创建对象
        HashMap<String, Object> globalMap = new HashMap<String, Object>();
        if (encoding == null)
            encoding = DefaultEncoding;
        //2.进行装载
        for (Object obj : objects)
            if (obj instanceof File) {
                //文件装载，key值是文件名。key相当于属性的作用域。
                File fileObject = (File) obj;
                InputStream stream = ResourcesUtil.getResourceAsStream(fileObject);
                globalMap.putAll(this.loadConfig(stream, encoding));//添加属性
            } else if (obj instanceof URL) {
                //URL装载，key值是getFile名。key相当于属性的作用域。
                URL urlObject = (URL) obj;
                InputStream stream = ResourcesUtil.getResourceAsStream(urlObject);
                globalMap.putAll(this.loadConfig(stream, encoding));//添加属性
            } else if (obj instanceof URI) {
                //URI装载，key值是getPath名。key相当于属性的作用域。
                URI uriObject = (URI) obj;
                InputStream stream = ResourcesUtil.getResourceAsStream(uriObject);
                globalMap.putAll(this.loadConfig(stream, encoding));//添加属性
            } else if (obj instanceof String) {
                //字符串装载
                String stringObject = (String) obj;
                List<InputStream> streams = ResourcesUtil.getResourcesAsStream(stringObject);
                for (InputStream stream : streams)
                    globalMap.putAll(this.loadConfig(stream, encoding));//添加属性
            } else if (obj instanceof InputStream) {
                //字符串装载
                InputStream streamObject = (InputStream) obj;
                globalMap.putAll(this.loadConfig(streamObject, encoding));//添加属性
            }
        return globalMap;
    }
    public Global createGlobal(String encoding, Object[] objects) throws IOException {
        Map<String, Object> dataMap = createMap(encoding, objects);
        return AbstractGlobal.newInterInstance(dataMap);
    };
}