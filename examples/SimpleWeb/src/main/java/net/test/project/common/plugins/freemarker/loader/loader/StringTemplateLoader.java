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
package net.test.project.common.plugins.freemarker.loader.loader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.test.project.common.plugins.freemarker.loader.FmTemplateLoader;
import net.test.project.common.plugins.freemarker.loader.IResourceLoader;
/**
 * 处理配置文件中添加的模板。
 * @version : 2011-9-14
 * @author 赵永春 (zyc@byshell.org) 
 */
public class StringTemplateLoader implements FmTemplateLoader, IResourceLoader {
    private Map<String, String_TemplateObject> objectMap = null;
    //
    public StringTemplateLoader() {
        this.objectMap = new HashMap<String, String_TemplateObject>();
    };
    public List<String> getKeys() {
        return new ArrayList<String>(objectMap.keySet());
    }
    public String getType() {
        return this.getClass().getSimpleName();
    }
    /**将字符串作为模板内容添加到装载器中。*/
    public void addTemplateAsString(String name, String templateString) {
        this.objectMap.put(name, new String_TemplateObject(templateString));
    }
    public Object findTemplateSource(String arg0) throws IOException {
        if (this.objectMap.containsKey(arg0) == false)
            return null;
        return this.objectMap.get(arg0);
    }
    public long getLastModified(Object arg0) {
        String_TemplateObject mto = (String_TemplateObject) arg0;
        return mto.lastModified();
    }
    public Reader getReader(Object arg0, String encoding) throws IOException {
        if (arg0 instanceof String_TemplateObject == false)
            return null;
        String_TemplateObject mto = (String_TemplateObject) arg0;
        mto.openObject();
        return mto.getReader(encoding);
    }
    public void closeTemplateSource(Object arg0) throws IOException {
        if (arg0 instanceof String_TemplateObject == true)
            ((String_TemplateObject) arg0).closeObject();
    }
    public URL getResource(String resourcePath) throws IOException {
        if (this.objectMap.containsKey(resourcePath) == false)
            return null;
        return new URL("template-mto://" + resourcePath);
    }
    public InputStream getResourceAsStream(String resourcePath) throws IOException {
        if (this.objectMap.containsKey(resourcePath) == false)
            return null;
        return this.objectMap.get(resourcePath).getInputStream();
    }
    public void resetState() {
        this.objectMap.clear();
    }
}
class String_TemplateObject {
    private String templateString = null;
    //
    public String_TemplateObject(String templateString) {
        this.templateString = templateString;
    }
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.templateString.getBytes());
    }
    public Reader getReader(String encoding) throws IOException {
        InputStream in = this.getInputStream();
        if (encoding == null)
            return new InputStreamReader(in, "utf-8");
        else
            return new InputStreamReader(in, encoding);
    }
    public long lastModified() {
        return new Date().getTime();
    }
    public void openObject() {
        // TODO Auto-generated method stub
    }
    public void closeObject() {
        // TODO Auto-generated method stub
    }
}