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
package org.more.core.copybean;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * Bean拷贝工具所能支持的类型基类，如果想动态向CopyBean中增加可以拷贝的Bean类型则需要编写相应的类型定义。
 * 编写的类型定义需要继承BeanType类。
 * Date : 2009-5-20
 * @author 赵永春
 */
public abstract class BeanType implements Serializable {
    /** 输出日志 */
    protected static transient ILog log = LogFactory.getLog("org_more_core_copybean");
    /**
     * 获取类型中bean里面的所有可读或可写属性Map。
     * @param object 目标对象。
     * @return 返回类型中bean里面的所有可读或可写属性Map。
     */
    Map<String, PropertyReaderWrite> getPropertys(Object object) {
        BeanType.log.debug("BeanType getPropertys at class =" + object.getClass().getName() + " obj= " + object);
        Hashtable<String, PropertyReaderWrite> map = new Hashtable<String, PropertyReaderWrite>();
        //
        Iterator<String> iterator = this.iteratorNames(object);
        while (iterator.hasNext()) {
            String ns = iterator.next();
            BeanType.log.debug("BeanType see property name is " + ns);
            PropertyReaderWrite rw = this.getPropertyRW(object, ns);
            if (rw != null)
                map.put(ns, rw);
            BeanType.log.debug("BeanType see property name is " + ns + " rw=" + rw);
        }
        BeanType.log.debug("BeanType see property coount " + map.size());
        return map;
    }
    /**
     * 获得Bean对象的属性名称迭代器。BeanType的getPropertys方法通过该迭代器迭代创建PropertyReaderWrite对象。
     * @return 返回Bean对象的属性名称迭代器。BeanType的getPropertys方法通过该迭代器迭代创建PropertyReaderWrite对象。
     */
    protected abstract Iterator<String> iteratorNames(Object obj);
    /**
     * 创建对象的某一特定属性读写器。该读写器对象用于对目标对象的属性进行读写的操作类。子类需要实现该方法。
     * @param obj 目标被读写的对象。
     * @param name 被读写操作的对象属性名。
     * @return 返回对象的属性读写器。该返回值可以为空，如果为空则忽略该属性。
     */
    protected abstract PropertyReaderWrite getPropertyRW(Object obj, String name);
    /**
     * 检查一个对象是否可以被当前类型所支持。系统通过枚举所有可支持的类型通过该方法确认使用哪个BeanType对象。
     * 一旦系统选种一个BeanType对象将忽略其他BeanType对象。所以在注册时需要注意顺序。新注册的所有类型都比
     * 系统默认的优先级要高。子类需要实现该方法。
     * @param object 被检查的对象。
     * @return 返回检查结果如果当前BeanType对象可以解析该对象则返回true否则返回false。
     */
    protected abstract boolean checkObject(Object object);
}