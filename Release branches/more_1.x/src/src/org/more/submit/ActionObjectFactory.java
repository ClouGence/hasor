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
/**
 * 该接口负责，创建查找一切Action相关资源，同时该负责解析Filter注解。<br/>
 * 不同的submit外壳提供程序需要根据自身所依托的容器提供不同的ActionObjectFactory接口实现。
 * @version 2010-1-9
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionObjectFactory {
    /**测试某个对象是否存在于容器，该对象可能是Action或者Filter也可能是其他对象。*/
    public boolean contains(String name);
    /**查找一个contains方法可以测试存在的对象。*/
    public Object findObject(String name);
    /**返回一个容器中所有对象名称迭代器*/
    public Iterator<String> getObjectNameIterator();
    /**获取contains方法可以测试存在的对象的类型。*/
    public Class<?> getObjectType(String name);
    /**查找并且返回一个Action过滤器对象。*/
    public ActionFilter getActionFilter(String filterName);
    /**根据action名查找这个action的所有公有过滤器。*/
    public Iterator<String> getPublicFilterNames(String actionName);
    /**根据action名查找这个action的所有私有过滤器。*/
    public Iterator<String> getPrivateFilterNames(String actionName);
}