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
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 集合形式的{@link DefineResource}接口实现类，该类将所有Bean定义数据都存放在内存中。
 * @version 2010-11-30
 * @author 赵永春 (zyc@byshell.org)
 */
public class ArrayDefineResource extends AbstractDefineResource {
    private static ILog                     log         = LogFactory.getLog(ArrayDefineResource.class);
    private ArrayList<String>               defineNames = new ArrayList<String>();                     //bean定义名称集合
    private Map<String, AbstractBeanDefine> defineMap   = new HashMap<String, AbstractBeanDefine>();   //bean定义Map
    //
    public AbstractBeanDefine getBeanDefine(String id) throws NoDefinitionException {
        if (id == null) {
            log.error("param id is null.");
            return null;
        }
        if (this.defineNames.contains(id) == false) {
            log.info("{%0} define is not exist.", id);
            return null;
        }
        return this.defineMap.get(id);
    };
    public boolean containsBeanDefine(String id) {
        if (id == null) {
            log.error("param id is null.");
            return false;
        }
        boolean res = this.defineNames.contains(id);
        log.debug("test {%0} define , exist = {%1}.", id, res);
        return res;
    };
    public List<String> getBeanDefinitionIDs() {
        return Collections.unmodifiableList((List<String>) this.defineNames);
    }
    public boolean isPrototype(String id) throws NoDefinitionException {
        if (this.containsBeanDefine(id) == false)
            throw new NoDefinitionException("bean " + id + " is not exist.");
        AbstractBeanDefine define = this.getBeanDefine(id);
        if (define.factoryMethod() == null && define.isSingleton() == false)
            return true;
        else
            return false;
    }
    public boolean isSingleton(String id) throws NoDefinitionException {
        if (this.containsBeanDefine(id) == false)
            throw new NoDefinitionException("bean " + id + " is not exist.");
        AbstractBeanDefine define = this.getBeanDefine(id);
        return define.isSingleton();
    }
    public boolean isFactory(String id) throws NoDefinitionException {
        if (this.containsBeanDefine(id) == false)
            throw new NoDefinitionException("bean " + id + " is not exist.");
        AbstractBeanDefine define = this.getBeanDefine(id);
        return (define.factoryMethod() == null) ? false : true;
    }
    public synchronized void addBeanDefine(AbstractBeanDefine define) throws NullPointerException, RepeateException {
        if (define == null) {
            log.warning("param define is null.");
            return;
        }
        String defineID = define.getID();
        if (this.containsBeanDefine(defineID) == true) {
            log.error("define {%0} is exist.", defineID);
            throw new RepeateException("define " + defineID + " is exist.");
        }
        this.throwEvent(Event.getEvent(AddDefineEvent.class), this, define);//新Bean定义，使用队列形式。
        log.info("add define id = {%0} , type = {%1}.", defineID, define.getBeanType());
        this.defineNames.add(defineID);
        this.defineMap.put(defineID, define);
    };
    public synchronized void clearDefine() {
        this.throwEvent(Event.getEvent(ClearDefineEvent.class), this);//销毁
        log.info("clearDefine!");
        this.defineNames.clear();
        this.defineMap.clear();
    };
    public boolean isReady() {
        return true;
    }
    public void toReady() throws Throwable {};
};