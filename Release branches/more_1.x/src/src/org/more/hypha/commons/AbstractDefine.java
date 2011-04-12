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
package org.more.hypha.commons;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.hypha.Plugin;
import org.more.hypha.PluginSet;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 该类是所有描述信息需要集成的父类，该类提供了扩展描述接口{@link BeanDefinePluginSet}接口的实现和{@link IAttribute}接口实现。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractDefine<T> implements IAttribute, PluginSet<T> {
    private IAttribute             attribute       = null; //属性
    private Map<String, Plugin<T>> pluginList      = null; //扩展配置描述
    private ArrayList<String>      pluginNamesList = null; //扩展配置描述
    //========================================================================================
    public Plugin<T> getPlugin(String name) {
        if (this.pluginList == null)
            return null;
        return this.pluginList.get(name);
    };
    public void setPlugin(String name, Plugin<T> plugin) {
        if (this.pluginList == null)
            this.pluginList = new HashMap<String, Plugin<T>>();
        if (this.pluginNamesList == null)
            this.pluginNamesList = new ArrayList<String>();
        //
        this.pluginList.put(name, plugin);
        this.pluginNamesList.add(name);
    };
    public void removePlugin(String name) {
        this.pluginList.remove(name);
        this.pluginNamesList.remove(name);
    };
    public List<String> getPluginNames() {
        return Collections.unmodifiableList(this.pluginNamesList);
    }
    //========================================================================================
    protected IAttribute getAttribute() {
        if (this.attribute == null)
            this.attribute = new AttBase();
        return this.attribute;
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
    };
}