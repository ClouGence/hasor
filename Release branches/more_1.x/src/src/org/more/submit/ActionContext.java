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
import java.util.Iterator;
import org.more.core.error.ExistException;
/**
 * 该接口负责创建并且返回{@link ActionInvoke}对象。除此之外action的生命周期管理也由该接口提供，
 * 但目前版本暂不支持生命周期管理。
 * @version 2010-7-27
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionContext {
    /**测试某个Action对象是否存在，参数是要测试的action对象名，如果存在返回true否则返回false。*/
    public boolean containsAction(String actionName);
    /**
     * 查找并且返回目标Action对象，如果无法查找或者查找action调用目标失败则会引发{@link ExistException}异常。
     * <br/>该方法会引发检测程序的所有环节。
     * @param actionName action对象名。
     * @param invoke action对象服务名。
     * @return 返回查找到的action调用对象。
     * @throws ExistException 如果要获取的action不存在或者找不到则会引发该异常。
     */
    public ActionInvoke findAction(String actionName, String invoke) throws ExistException;
    /** 获取一个指定的Action类型，参数为action对象名。*/
    public Class<?> getActionType(String actionName);
    /** 通过这个迭代器可以迭代所有Action对象名。*/
    public Iterator<String> getActionNameIterator();
    /**根据属性名和action名获取指定的Action。*/
    public Object getActionProperty(String actionName, String property);
};