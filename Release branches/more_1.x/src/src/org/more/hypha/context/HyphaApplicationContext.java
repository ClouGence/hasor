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
import org.more.hypha.ApplicationContext;
import org.more.hypha.DefineResource;
import org.more.hypha.Event;
import org.more.hypha.EventListener;
import org.more.hypha.beans.AbstractBeanDefine;
/**
 * 
 * @version 2010-11-30
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class HyphaApplicationContext implements ApplicationContext {
    public HyphaApplicationContext(ArrayDefineResource arrayDefineResource, Object context) {
        // TODO Auto-generated constructor stub
    }
    @Override
    public void addEventListener(Class<? extends Event> eventType, EventListener listener) {
        // TODO Auto-generated method stub
    }
    @Override
    public void doEvent(Event event) {
        // TODO Auto-generated method stub
    }
    @Override
    public boolean contains(String name) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void setAttribute(String name, Object value) {
        // TODO Auto-generated method stub
    }
    @Override
    public Object getAttribute(String name) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void removeAttribute(String name) {
        // TODO Auto-generated method stub
    }
    @Override
    public String[] getAttributeNames() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void clearAttribute() {
        // TODO Auto-generated method stub
    }
    @Override
    public List<String> getBeanDefinitionNames() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public AbstractBeanDefine getBeanDefinition(String name) throws NoDefinitionException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public DefineResource getBeanResource() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public ClassLoader getBeanClassLoader() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean containsBean(String name) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public Object getBean(String name, Object... objects) throws NoDefinitionException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Class<?> getBeanType(String name) throws NoDefinitionException {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public boolean isPrototype(String name) throws NoDefinitionException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isSingleton(String name) throws NoDefinitionException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isFactory(String name) throws NoDefinitionException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public boolean isTypeMatch(String name, Class<?> targetType) throws NoDefinitionException {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public void init() throws Exception {
        // TODO Auto-generated method stub
    }
    @Override
    public void destroy() throws Exception {
        // TODO Auto-generated method stub
    }
}
