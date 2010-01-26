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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.more.DoesSupportException;
import org.more.InvokeException;
import org.more.NoDefinitionException;
import org.more.RepeateException;
import org.more.beans.BeanResource;
import org.more.beans.info.BeanDefinition;
import org.more.beans.info.CreateTypeEnum;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 
 * @version 2009-11-21
 * @author 赵永春 (zyc@byshell.org)
 */
public class ArrayResource implements BeanResource, IAttribute {
    //========================================================================================Field
    /**  */
    private static final long               serialVersionUID    = -1650492842757900558L;
    private HashMap<String, BeanDefinition> caheBeans           = null;
    private LinkedList<String>              strartInitBeans     = null;                 //
    private String                          resourceDescription = null;                 //
    private String                          sourceName          = null;                 //
    private IAttribute                      prop                = null;                 //
    private boolean                         isInit              = false;                //
    /**当执行了init方法之后该值是true，当执行了destroy之后该值就是false。*/
    public boolean isInit() {
        return isInit;
    }
    //==================================================================================Constructor
    public ArrayResource(String sourceName, BeanDefinition[] definition) {
        if (definition != null)
            for (BeanDefinition def : definition) {
                if (def.isLazyInit() == false)
                    strartInitBeans.add(def.getName());
                this.caheBeans.put(def.getName(), def);
            }
        this.sourceName = sourceName;
    }
    //=====================================================================================Core Core
    @Override
    public boolean containsBeanDefinition(String name) {
        return this.caheBeans.containsKey(name);
    }
    @Override
    public BeanDefinition getBeanDefinition(String name) {
        if (this.caheBeans.containsKey(name) == true)
            return this.caheBeans.get(name);
        try {
            BeanDefinition bean = this.findBeanDefinition(name);
            if (bean != null)
                return bean;
        } catch (Exception e) {
            if (e instanceof RuntimeException == true)
                throw (RuntimeException) e;
            throw new InvokeException(e);
        }
        throw new NoDefinitionException("不存在名称为[" + name + "]的bean定义。");
    }
    @Override
    public List<String> getBeanDefinitionNames() {
        return new ArrayList<String>(caheBeans.keySet());
    }
    protected BeanDefinition findBeanDefinition(String name) throws Exception {
        return null;
    }
    //==========================================================================================Job
    @Override
    public List<String> getStrartInitBeanDefinitionNames() {
        return new ArrayList<String>(strartInitBeans);
    }
    /**增加一个bean定义到bean定义静态缓存区，该方法不会检查bean名称重复问题。*/
    public void addBeanDefinition(BeanDefinition beanDef) {
        String name = beanDef.getName();
        if (this.caheBeans.containsKey(name) == true) {
            throw new RepeateException("无法新增名称为[" + name + "]的bean定义，原因是已经存在了一个同样名称的bean定义。");
        } else
            this.caheBeans.put(beanDef.getName(), beanDef);
    }
    /**从静态缓存区删除一个bean定义。*/
    public void removeBeanDefinition(String beanDefName) {
        this.caheBeans.remove(beanDefName);
    }
    @Override
    public synchronized void destroy() {
        this.prop.clearAttribute();
        this.caheBeans.clear();
        this.strartInitBeans.clear();
        this.resourceDescription = null;
        this.isInit = false;
    }
    @Override
    public synchronized void init() throws Exception {
        this.isInit = true;
        this.caheBeans = new HashMap<String, BeanDefinition>();
        this.strartInitBeans = new LinkedList<String>(); //
        this.prop = new AttBase(); //
    }
    @Override
    public synchronized void clearCache() throws DoesSupportException {
        throw new DoesSupportException("ArrayResource类型资源对象不支持该方法。");
    }
    @Override
    public boolean isFactory(String name) {
        BeanDefinition b = this.getBeanDefinition(name);
        return (b.getCreateType() == CreateTypeEnum.Factory) ? true : false;
    }
    @Override
    public boolean isPrototype(String name) {
        BeanDefinition b = this.getBeanDefinition(name);
        return (b.getCreateType() == CreateTypeEnum.New) ? true : false;
    }
    @Override
    public boolean isSingleton(String name) {
        return this.getBeanDefinition(name).isSingleton();
    }
    @Override
    public String getResourceDescription() {
        return this.resourceDescription;
    }
    public void setResourceDescription(String resourceDescription) {
        this.resourceDescription = resourceDescription;
    }
    protected void setSourceName(String sourceName) {
        this.sourceName = sourceName;
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
    public boolean isCacheBeanMetadata() {
        return true;
    }
    //==========================================================================================Impl Att
    @Override
    public Object getAttribute(String key) throws DoesSupportException {
        return prop.getAttribute(key);
    }
    @Override
    public void clearAttribute() {
        this.prop.clearAttribute();
    }
    @Override
    public boolean contains(String name) {
        return this.prop.contains(name);
    }
    @Override
    public String[] getAttributeNames() {
        return this.prop.getAttributeNames();
    }
    @Override
    public void removeAttribute(String name) {
        this.prop.removeAttribute(name);
    }
    @Override
    public void setAttribute(String name, Object value) {
        this.prop.setAttribute(name, value);
    }
    @Override
    public void reload() throws Exception {
        this.destroy();
        this.init();
    }
}