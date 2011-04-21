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
package org.more.hypha.context.array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.NoDefinitionException;
import org.more.RepeateException;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.DefineResource;
import org.more.hypha.Event;
import org.more.hypha.context.AbstractDefineResource;
import org.more.hypha.event.AddBeanDefineEvent;
import org.more.hypha.event.ClearALLDefineEvent;
/**
 * 集合形式的{@link DefineResource}接口实现类，该类将所有Bean定义数据都存放在内存中。
 * @version 2010-11-30
 * @author 赵永春 (zyc@byshell.org)
 */
public class ArrayDefineResource extends AbstractDefineResource {
    private ArrayList<String>               defineNames = new ArrayList<String>();                  //bean定义名称集合
    private Map<String, AbstractBeanDefine> defineMap   = new HashMap<String, AbstractBeanDefine>(); //bean定义Map
    //
    public AbstractBeanDefine getBeanDefine(String id) throws NoDefinitionException {
        if (this.defineNames.contains(id) == false)
            throw new NoDefinitionException("不存在id为[" + id + "]的Bean定义。");
        return this.defineMap.get(id);
    };
    public synchronized void addBeanDefine(AbstractBeanDefine define) throws NoDefinitionException {
        if (this.defineNames.contains(define.getID()) == true)
            throw new RepeateException("[" + define.getID() + "]Bean定义重复。");
        this.getEventManager().doEvent(Event.getEvent(AddBeanDefineEvent.class), this, define);//新Bean定义，使用队列形式。
        this.defineNames.add(define.getID());
        this.defineMap.put(define.getID(), define);
    };
    public boolean containsBeanDefine(String id) {
        return this.defineNames.contains(id);
    };
    public synchronized List<String> getBeanDefinitionIDs() {
        return Collections.unmodifiableList((List<String>) this.defineNames);
    }
    public boolean isPrototype(String id) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefine(id);
        if (define.factoryMethod() == null && define.isSingleton() == false)
            return true;
        else
            return false;
    }
    public boolean isSingleton(String id) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefine(id);
        return define.isSingleton();
    }
    public boolean isFactory(String id) throws NoDefinitionException {
        AbstractBeanDefine define = this.getBeanDefine(id);
        return (define.factoryMethod() == null) ? false : true;
    }
    public synchronized void clearDefine() {
        this.getEventManager().doEvent(Event.getEvent(ClearALLDefineEvent.class), this);//销毁
        this.defineNames.clear();
        this.defineMap.clear();
    }
};