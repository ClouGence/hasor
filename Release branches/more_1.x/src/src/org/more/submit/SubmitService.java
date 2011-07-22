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
import org.more.hypha.Service;
import org.more.util.attribute.IAttribute;
/**
 * Submit 4.0的服务接口。
 * @version : 2011-7-14
 * @author 赵永春 (zyc@byshell.org)
 */
public interface SubmitService extends IAttribute, Service {
    /**注册命名空间。*/
    public void regeditNameSpace(String prefix, ActionContext context);
    /**解除某个命名空间的注册。*/
    public void unRegeditNameSpace(String prefix);
    /**改变默认命名空间。*/
    public void changeDefaultNameSpace(String prefix);
    /**根据命名空间前缀获取其{@link ActionContext}对象。*/
    public ActionContext getNameSpace(String prefix);
    /**获取默认命名空间名。*/
    public String getDefaultNameSpaceString();
    /**获取默认命名空间对象。*/
    public ActionContext getDefaultNameSpace();
    /**获取一个action对象，通过action对象可以执行action调用。该字符串格式类似如下：ac://package.package.package.action/param/param */
    public ActionObject getActionObject(String url) throws Throwable;
    /**获取一个action对象，通过action对象可以执行action调用。该字符串格式类似如下：ac://package.package.package.action/param/param*/
    public ActionObject getActionObject(URI uri) throws Throwable;
    /**注册一个作用域，重复注册同一个名称的作用域则会产生替换。*/
    public void regeditScope(String scopeName, IAttribute scope);
    /**获取已经注册的作用域，如果不存在该作用域则返回一个null。*/
    public IAttribute getScope(String scopeName);
    /**获取一个{@link IAttribute}接口对象，该接口对象是一个封装了多个作用域的接口对象。*/
    public IAttribute getScopeStack();
    /*-----------------------------------*/
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
}