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
package org.more.hypha.context;
import java.util.List;
import org.more.NoDefinitionException;
import org.more.hypha.AbstractEventManager;
import org.more.hypha.ApplicationContext;
import org.more.hypha.DefineResource;
import org.more.hypha.Event;
import org.more.hypha.EventListener;
import org.more.hypha.EventManager;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 
 * @version 2010-11-30
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class HyphaApplicationContext implements ApplicationContext {
    private DefineResource defineResource = null;
    private Object         context        = null;
    private EventManager   eventManager   = new AbstractEventManager() {};
    /*------------------------------------------------------------*/
    public HyphaApplicationContext(DefineResource defineResource, Object context) {
        this.defineResource = defineResource;
        this.context = context;
    };
    public List<String> getBeanDefinitionNames() {
        return this.defineResource.getBeanDefineNames();
    };
    public AbstractBeanDefine getBeanDefinition(String id) throws NoDefinitionException {
        return this.defineResource.getBeanDefine(id);
    };
    public DefineResource getBeanResource() {
        return this.defineResource;
    };
    public ClassLoader getBeanClassLoader() {
        return this.defineResource.getClassLoader();
    };
    public boolean containsBean(String id) {
        return this.defineResource.containsBeanDefine(id);
    };
    public boolean isPrototype(String id) throws NoDefinitionException {
        return this.defineResource.isPrototype(id);
    };
    public boolean isSingleton(String id) throws NoDefinitionException {
        return this.defineResource.isSingleton(id);
    };
    public boolean isFactory(String id) throws NoDefinitionException {
        return this.defineResource.isFactory(id);
    };
    public boolean isTypeMatch(String id, Class<?> targetType) throws NoDefinitionException {
        return false;//TODO
    };
    public void init() throws Exception {
        if (this.defineResource.isReady() == false)
            System.out.println();
        //TODO
    };
    public void destroy() throws Exception {
        //TODO
    };
    public Object getBean(String id, Object... objects) throws NoDefinitionException {
        return null;//TODO
    };
    public Class<?> getBeanType(String id) throws NoDefinitionException {
        return null;//TODO
    };
    public Object getContext() {
        return this.context;
    };
    /*------------------------------------------------------------*/
    private AttBase attBase = null;
    protected IAttribute getAttribute() {
        if (this.attBase == null)
            this.attBase = new AttBase();
        return this.attBase;
    }
    public boolean contains(String name) {
        return this.getAttribute().contains(name);
    };
    public void setAttribute(String name, Object value) {
        this.getAttribute().setAttribute(name, value);
    };
    public Object getAttribute(String name) {
        return this.getAttribute().getAttribute(name);
    };
    public void removeAttribute(String name) {
        this.getAttribute().removeAttribute(name);
    };
    public String[] getAttributeNames() {
        return this.getAttribute().getAttributeNames();
    };
    public void clearAttribute() {
        this.getAttribute().clearAttribute();
    }
    /*------------------------------------------------------------*/
    public void addEventListener(Class<? extends Event> eventType, EventListener listener) {
        this.eventManager.addEventListener(eventType, listener);
    }
    public void doEvent(Event event) {
        this.eventManager.doEvent(event);
    };
}