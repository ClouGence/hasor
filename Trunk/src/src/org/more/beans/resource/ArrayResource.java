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
package org.more.beans.resource;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.more.DoesSupportException;
import org.more.beans.BeanResource;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.CreateTypeEnum;
import org.more.util.attribute.AttBase;
/**
 * 
 * <br/>Date : 2009-11-21
 * @author 赵永春
 */
public class ArrayResource extends AttBase implements BeanResource {
    //========================================================================================Field
    /**  */
    private static final long               serialVersionUID    = -1650492842757900558L;                //
    private HashMap<String, BeanDefinition> beans               = new HashMap<String, BeanDefinition>(); //
    private String                          resourceDescription = null;                                 //
    private String                          sourceName          = null;                                 //
    private LinkedList<String>              strartInitBeans     = new LinkedList<String>();
    //==================================================================================Constructor
    public ArrayResource(BeanDefinition[] definition) {
        if (definition != null)
            for (BeanDefinition def : definition) {
                if (def.isLazyInit() == false)
                    strartInitBeans.add(def.getName());
                this.beans.put(def.getName(), def);
            }
    }
    //==========================================================================================Job
    @Override
    public void clearCache() throws DoesSupportException {
        throw new DoesSupportException("ArrayResource类型资源对象不支持该方法。");
    }
    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beans.get(name);
    }
    @Override
    public List<String> getBeanDefinitionNames() {
        ArrayList<String> al = new ArrayList<String>(0);
        al.addAll(this.beans.keySet());
        return al;
    }
    @Override
    public List<String> getStrartInitBeanDefinitionNames() {
        return strartInitBeans;
    }
    @Override
    public boolean isFactory(String name) {
        if (this.beans.get(name).getCreateType() == CreateTypeEnum.Factory)
            return true;
        else
            return false;
    }
    @Override
    public boolean isPrototype(String name) {
        if (this.beans.get(name).getCreateType() == CreateTypeEnum.New)
            return true;
        else
            return false;
    }
    @Override
    public boolean isSingleton(String name) {
        return this.beans.get(name).isSingleton();
    }
    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beans.containsKey(name);
    }
    @Override
    public Object getAttribute(String key) throws DoesSupportException {
        return super.getAttribute(key);
    }
    @Override
    public String getResourceDescription() {
        return this.resourceDescription;
    }
    public void setResourceDescription(String resourceDescription) {
        this.resourceDescription = resourceDescription;
    }
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    @Override
    public File getSourceFile() {
        return null;
    }
    @Override
    public String getSourceName() {
        return sourceName;
    }
    @Override
    public URI getSourceURI() {
        return null;
    }
    @Override
    public URL getSourceURL() {
        return null;
    }
    @Override
    public boolean isCacheBeanMetadata() {
        return true;
    }
}
