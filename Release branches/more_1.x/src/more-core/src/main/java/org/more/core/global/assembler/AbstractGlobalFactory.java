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
package org.more.core.global.assembler;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.more.core.global.Global;
import org.more.core.global.GlobalFactory;
import org.more.util.ResourcesUtil;
/**
 * 
 * @version : 2012-2-28
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractGlobalFactory implements GlobalFactory {
    protected abstract Map<String, Object> createProperties(InputStream stream, String encoding) throws Throwable;
    /*------------------------------------------------------------------------*/
    public Global createGlobal(String encoding, Object... objects) throws Throwable {
        Global global = Global.newInterInstance();
        //2.进行装载
        for (Object obj : objects)
            if (obj instanceof File) {
                //文件装载，key值是文件名。key相当于属性的作用域。
                File fileObject = (File) obj;
                InputStream stream = ResourcesUtil.getResourceAsStream(fileObject);
                global.addScope(fileObject.getName(), this.createProperties(stream, encoding));//添加属性
            } else if (obj instanceof URL) {
                //URL装载，key值是getFile名。key相当于属性的作用域。
                URL urlObject = (URL) obj;
                InputStream stream = ResourcesUtil.getResourceAsStream(urlObject);
                global.addScope(urlObject.getPath(), this.createProperties(stream, encoding));//添加属性
            } else if (obj instanceof URI) {
                //URI装载，key值是getPath名。key相当于属性的作用域。
                URI uriObject = (URI) obj;
                InputStream stream = ResourcesUtil.getResourceAsStream(uriObject);
                global.addScope(uriObject.getPath(), this.createProperties(stream, encoding));//添加属性
            } else if (obj instanceof String) {
                //字符串装载
                String stringObject = (String) obj;
                String name = new File(stringObject).getName();
                List<InputStream> streams = ResourcesUtil.getResourcesAsStream(stringObject);
                for (InputStream stream : streams)
                    global.addScope(name, this.createProperties(stream, encoding));//添加属性
            }
        return global;
    }
};