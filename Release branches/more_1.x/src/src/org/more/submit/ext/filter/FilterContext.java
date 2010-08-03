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
package org.more.submit.ext.filter;
import java.util.Iterator;
import org.more.submit.ActionContext;
/**
 * FilterContext接口是一个容器接口。由于在基本submit支持中是不包括filter特性的 ，因此filter特性的功能是通过给
 * {@link ActionContext}对象增加filter装饰而提供。filter装饰器需要通过这个接口来查找和管理filter。
 * @version : 2010-8-2
 * @author 赵永春(zyc@byshell.org)
 */
public interface FilterContext {
    /**测试某个filter对象是否存在，参数是要测试的filter对象名，如果存在返回true否则返回false。*/
    public boolean containsFilter(String filterName);
    /**查找并且返回一个filter过滤器对象。 */
    public ActionFilter findFilter(String filterName);
    /** 获取一个指定的filter类型，参数为filter对象名。*/
    public Class<?> getFilterType(String filterName);
    /** 通过这个迭代器可以迭代所有filter对象名。*/
    public Iterator<String> getFilterNameIterator();
    /**根据属性名和filterName名获取指定的filter属性。*/
    public Object getFilterProperty(String filterName, String property);
}