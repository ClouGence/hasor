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
package org.more.hypha.beans;
import java.util.Collection;
import org.more.hypha.PluginSet;
import org.more.util.attribute.IAttribute;
/**
 * 该接口用于定义在bean上的方法。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AbstractMethodDefine extends PluginSet<AbstractMethodDefine>, IAttribute {
    /**返回方法的代理名称，代理名称是用于索引方法的目的。*/
    public String getName();
    /**返回方法的真实名称，该属性是表示方法的真实方法名。*/
    public String getCodeName();
    /**返回方法的参数列表描述，返回的集合是只读的。*/
    public Collection<? extends AbstractPropertyDefine> getParams();
    /**获取这个方法所属的bean定义*/
    public AbstractBeanDefine getForBeanDefine();
}