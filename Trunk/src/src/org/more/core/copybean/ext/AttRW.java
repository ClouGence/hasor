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
package org.more.core.copybean.ext;
import java.util.ArrayList;
import java.util.Iterator;
import org.more.core.copybean.BeanType;
import org.more.core.copybean.PropertyReaderWrite;
import org.more.util.attribute.IAttribute;
/**
 * IAttribute类读写器。使用该类作为读写器可以实现从IAttribute对象中拷贝属性或者向IAttribute中拷贝属性。
 * @version 2009-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class AttRW extends BeanType {
    /**  */
    private static final long serialVersionUID = 5550209216691841191L;
    @Override
    public boolean checkObject(Object object) {
        return object instanceof IAttribute;
    }
    @Override
    protected Iterator<String> iteratorNames(Object obj) {
        IAttribute att = (IAttribute) obj;
        ArrayList<String> ns = new ArrayList<String>(0);
        for (String n : att.getAttributeNames())
            ns.add(n);
        return ns.iterator();
    }
    @Override
    protected PropertyReaderWrite getPropertyRW(Object obj, String name) {
        AttReaderWrite prw = new AttReaderWrite();
        prw.setName(name);
        prw.setObject(obj);
        return prw;
    }
}
/**
 * IAttribute类型的属性读写器
 * Date : 2009-5-21
 * @author 赵永春
 */
class AttReaderWrite extends PropertyReaderWrite {
    /**  */
    private static final long serialVersionUID = -2857886652147342020L;
    @Override
    public Object get() {
        IAttribute att = (IAttribute) this.getObject();
        return att.getAttribute(this.getName());
    }
    @Override
    public void set(Object value) {
        IAttribute att = (IAttribute) this.getObject();
        att.setAttribute(this.getName(), value);
    }
    @Override
    public Class<?> getPropertyClass() {
        return Object.class;
    }
}