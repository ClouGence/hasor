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
package org.more.submit;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.more.core.classcode.EngineToos;
import org.more.core.error.ExistException;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.util.attribute.Attribute;
import org.more.util.attribute.DecSequenceAttribute;
import org.more.util.attribute.IAttribute;
/**
 * submit v4.0的服务接口。
 * @version : 2012-3-10
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class SubmitService extends Attribute<Object> {
    private static Log                      log           = LogFactory.getLog(SubmitService.class);
    private Map<String, ActionContext>      acList        = new HashMap<String, ActionContext>();
    private String                          defaultSpace  = "default";
    private Map<String, IAttribute<Object>> scopeMap      = new HashMap<String, IAttribute<Object>>();
    private DecSequenceAttribute<Object>    scopeStack    = new DecSequenceAttribute<Object>();
    //
    private Map<String, ResultProcess>      processMap    = new HashMap<String, ResultProcess>();
    private List<String>                    processMatch  = new ArrayList<String>();
    private ResultProcess                   defaultResult = null;
    //
    /**注册命名空间。*/
    public void regeditSpace(String prefix, ActionContext context) {
        if (this.acList.containsKey(prefix) == true)
            log.debug("regeditSpace for ActionContext to {%0}. replace.", prefix);
        else
            log.debug("regeditSpace for ActionContext to {%0}.", prefix);
        this.acList.put(prefix, context);
    };
    /**解除某个命名空间的注册。*/
    public void unRegeditSpace(String prefix) {
        if (this.acList.containsKey(prefix) == true) {
            log.debug("unRegeditSpace {%0}.", prefix);
            acList.remove(prefix);
        }
    };
    /**改变默认命名空间。*/
    public void changeDefaultSpace(String defaultSpace) {
        this.defaultSpace = defaultSpace;
    };
    /**根据命名空间前缀获取其{@link ActionContext}对象。*/
    public ActionContext getSpace(String prefix) {
        if (this.acList.containsKey(prefix) == false)
            return null;
        return this.acList.get(prefix);
    };
    /**获取默认命名空间名。*/
    public String getDefaultSpaceString() {
        return this.defaultSpace;
    };
    /**获取默认命名空间对象。*/
    public ActionContext getDefaultSpace() {
        return this.getSpace(defaultSpace);
    };
    /**获取一个action对象，通过action对象可以执行action调用。该字符串格式类似如下：ac://actionnamepackage.package.package.action/param/param */
    public ActionObject getActionObject(String url) throws Throwable {
        return this.getActionObject(new URI(url));
    };
    /**获取一个action对象，通过action对象可以执行action调用。该字符串格式类似如下：ac://package.package.package.action/param/param*/
    public ActionObject getActionObject(URI uri) throws Throwable {
        if (uri == null)
            return null;
        ActionContext ac = null;
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals("") == true)
            ac = this.getDefaultSpace();
        else
            ac = this.acList.get(scheme);
        if (ac == null) {
            log.debug("Space “{%0}” support is not exist.", scheme);
            throw new ExistException("Space “" + scheme + "” support is not exist.");
        }
        //
        String authority = uri.getAuthority();
        String actionKey = EngineToos.splitPackageName(authority);
        String methodKey = EngineToos.splitSimpleName(authority);
        //
        ActionInvoke invoke = ac.getActionInvoke(actionKey, methodKey);
        if (invoke == null)
            return null;
        return this.createActionObject(uri, invoke);
    };
    /**获取已经注册的作用域，如果不存在该作用域则返回一个null。*/
    public IAttribute<Object> getScope(String scopeName) {
        return this.scopeMap.get(scopeName);
    };
    /**注册一个作用域，重复注册同一个名称的作用域则会产生替换。*/
    public void regeditScope(String scopeName, IAttribute<Object> scope) {
        if (scopeName == null || scope == null) {
            log.warning("regeditScope error , scopeName or scope is null.");
            return;
        }
        this.scopeStack.putAtt(scope);
        this.scopeMap.put(scopeName, scope);
    };
    /**注册一个结果处理器，当返回的结果是一个字符串时才会使用结果处理器进行处理。*/
    public void addResult(String match, ResultProcess process) {
        if (match == null || process == null)
            return;
        this.processMap.put(match, process);
        this.processMatch.add(match);
    };
    /**设置默认的结果处理器。*/
    public void setDefaultResult(ResultProcess defaultResult) {
        this.defaultResult = defaultResult;
    };
    /*-------------------------------------------------------------------------------------------*/
    //    /**获取一个{@link IAttribute}接口对象，该接口对象是一个封装了多个作用域的接口对象。*/
    //    private IAttribute<Object> getScopeStack() {
    //        return this.scopeStack;
    //    };
    ResultProcess getResultProcess(String name) {
        //1.确定m
        String m = null;
        for (String match : this.processMatch)
            if (name.matches(match) == true) {
                m = match;
                break;
            }
        //获取Result
        ResultProcess rp = null;
        if (m != null)
            rp = this.processMap.get(m);
        if (rp == null)
            rp = this.defaultResult;
        return rp;
    };
    /*-----------------------------------*/
    protected ActionObject createActionObject(URI uri, ActionInvoke invoke) {
        /*日后的扩展在这里开发*/
        return new ActionObject(invoke, this, uri);
    };
    protected ActionStack createStack(URI uri, ActionStack onStack, Map<String, Object> params) {
        ActionStack stack = null;
        if (onStack != null)
            stack = new ActionStack(uri, onStack, this);
        else
            stack = new ActionStack(uri, this);
        stack.putMap(params);
        return stack;
    };
};