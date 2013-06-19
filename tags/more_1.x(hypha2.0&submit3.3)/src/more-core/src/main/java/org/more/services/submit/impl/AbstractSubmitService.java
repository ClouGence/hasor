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
package org.more.services.submit.impl;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.more.core.error.ExistException;
import org.more.core.error.FormatException;
import org.more.core.error.ResourceException;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.commons.AbstractService;
import org.more.services.submit.ActionContext;
import org.more.services.submit.ActionInvoke;
import org.more.services.submit.ActionObject;
import org.more.services.submit.ActionPackage;
import org.more.services.submit.ActionStack;
import org.more.services.submit.Result;
import org.more.services.submit.ResultProcess;
import org.more.services.submit.SubmitService;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
import org.more.util.attribute.SequenceStack;
/**
 * 默认{@link SubmitService SubmitService接口实现}，是submit v4.0的服务提提供类。
 * @version : 2011-7-14
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractSubmitService extends AbstractService implements SubmitService {
    private static final long                     serialVersionUID = -2210504764611831806L;
    private static Log                            log              = LogFactory.getLog(AbstractSubmitService.class);
    private Map<String, ActionContext>            acList           = new HashMap<String, ActionContext>();
    private String                                defaultNameSpace = null;
    private Map<String, IAttribute<?>>            scopeMap         = new HashMap<String, IAttribute<?>>();
    private SequenceStack<Object>                 scopeStack       = new SequenceStack<Object>();
    private IAttribute<Object>                    thisAttribute    = new AttBase<Object>();
    //
    private Map<String, ActionPackage>            packages         = new HashMap<String, ActionPackage>();
    //
    private Map<String, ResultProcess<Result<?>>> processMap       = new HashMap<String, ResultProcess<Result<?>>>();
    private ResultProcess<Result<?>>              defaultResult    = null;
    //
    public void regeditNameSpace(String prefix, ActionContext context) {
        if (this.acList.containsKey(prefix) == true)
            log.debug("regeditNameSpace for ActionContext to {%0}. replace.", prefix);
        else
            log.debug("regeditNameSpace for ActionContext to {%0}.", prefix);
        this.acList.put(prefix, context);
    };
    public void unRegeditNameSpace(String prefix) {
        if (this.acList.containsKey(prefix) == true) {
            log.debug("unRegeditNameSpace {%0}.", prefix);
            acList.remove(prefix);
        }
    };
    public ActionContext getNameSpace(String prefix) {
        if (this.acList.containsKey(prefix) == false)
            return null;
        return this.acList.get(prefix);
    };
    //
    public ActionObject getActionObject(String url) throws Throwable {
        URI u = new URI(url);
        if (u.getScheme() == null)
            u = new URI(this.defaultNameSpace + "://" + url);
        return this.getActionObject(u);
    };
    public ActionPackage definePackage(String packageName) {
        if (packageName == null)
            packageName = "";
        if (packageName.indexOf('/') > 0)
            throw new FormatException("包名中不能存在字符‘/’");
        ActionPackage pack = this.packages.get(packageName);
        if (pack == null) {
            pack = new DefaultPackage(packageName);
            this.packages.put(packageName, pack);
        }
        return pack;
    };
    public ActionObject getActionObject(URI uri) throws Throwable {
        if (uri == null)
            return null;
        ActionContext ac = null;
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals("") == true)
            ac = this.getDefaultNameSpace();
        else
            ac = this.acList.get(scheme);
        if (ac == null) {
            log.debug("namespace “{%0}” support is not exist.", scheme);
            throw new ExistException("namespace “" + scheme + "” support is not exist.");
        }
        //先从默认包中获取actio路径。
        String name = uri.getAuthority();
        Method actionPath = this.definePackage("").getActionPath("/" + name);
        if (actionPath == null)
            //name作为包名获取action地址。
            actionPath = this.definePackage(name).getActionPath(uri.getPath());
        if (actionPath == null)
            throw new ResourceException("默认包中不存在该action映射，同时也没有定义该名称的包。[" + name + "]");
        //
        ActionInvoke invoke = ac.getAction(uri, actionPath);
        //
        if (invoke == null)
            return null;
        return this.createActionObject(uri, invoke);
    };
    public ActionContext getDefaultNameSpace() {
        return this.getNameSpace(defaultNameSpace);
    };
    public void changeDefaultNameSpace(String defaultNameSpace) {
        this.defaultNameSpace = defaultNameSpace;
    };
    public String getDefaultNameSpaceString() {
        return this.defaultNameSpace;
    };
    public IAttribute<?> getScope(String scopeName) {
        return this.scopeMap.get(scopeName);
    };
    public void regeditScope(String scopeName, IAttribute<?> scope) {
        if (scopeName == null || scope == null) {
            log.warning("regeditScope error , scopeName or scope is null.");
            return;
        }
        this.scopeStack.putStack((IAttribute<Object>) scope);
        this.scopeMap.put(scopeName, scope);
    };
    public IAttribute<?> getScopeStack() {
        return this.scopeStack;
    };
    public void addResult(String name, ResultProcess<Result<?>> process) {
        if (name == null || process == null)
            return;
        this.processMap.put(name.toLowerCase(), process);
    };
    public void setDefaultResult(ResultProcess<Result<?>> defaultResult) {
        this.defaultResult = defaultResult;
    };
    public ResultProcess<Result<?>> getResultProcess(String name) {
        name = name.toLowerCase();
        //获取Result
        ResultProcess<Result<?>> rp = this.processMap.get(name);
        if (rp == null)
            rp = this.defaultResult;
        return rp;
    };
    /*-----------------------------------*/
    public boolean contains(String name) {
        return this.thisAttribute.contains(name);
    }
    public void setAttribute(String name, Object value) {
        this.thisAttribute.setAttribute(name, value);
    }
    public Object getAttribute(String name) {
        return this.thisAttribute.getAttribute(name);
    }
    public void removeAttribute(String name) {
        this.thisAttribute.removeAttribute(name);
    }
    public String[] getAttributeNames() {
        return this.thisAttribute.getAttributeNames();
    }
    public void clearAttribute() {
        this.thisAttribute.clearAttribute();
    }
    public Map<String, Object> toMap() {
        return this.thisAttribute.toMap();
    }
    /*-----------------------------------*/
    protected abstract ActionObject createActionObject(URI uri, ActionInvoke invoke);
    protected abstract ActionStack createStack(URI uri, ActionStack onStack, Map<String, ?> params);
    //
    //
    //
    //    public Session getSession(String sessionID); 
    //    public Session getSession();
    //    public SessionContext getSessionContext();
    //
    //    public Sate getState(String stateID);
    //    public Sate getState();
    //    public SateContext getStateContext();
    // 
    //    public FormContext getFormContext();
    //
    //    public Route getRoute(String match);
    //    public Route setRoute(Route route, String match);
};