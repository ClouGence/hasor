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
import java.util.List;
import org.more.core.copybean.BeanType;
import org.more.core.copybean.PropertyReaderWrite;
/**
 * List类读写器。使用该类作为读写器可以实现从List对象中拷贝属性或者向List中拷贝属性。
 * @version 2009-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings("unchecked")
public class ListRW extends BeanType {
    /**  */
    private static final long serialVersionUID = -3987939968587042522L;
    @Override
    public boolean checkObject(Object object) {
        return object instanceof List;
    }
    @Override
    protected Iterator<String> iteratorNames(Object obj) {
        //al没有任何作用只是为了得到一个迭代器，并且可以进入到getPropertyRW方法
        ArrayList al = new ArrayList();
        al.add("1");
        return al.iterator();
    }
    @Override
    protected PropertyReaderWrite getPropertyRW(Object obj, String name) {
        ListReaderWrite prw = new ListReaderWrite();
        prw.setName(name);
        prw.setObject(obj);
        return prw;
    }
}
/**
 * List类型的属性读写器
 * Date : 2009-5-21
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
class ListReaderWrite extends PropertyReaderWrite {
    /**  */
    private static final long serialVersionUID = 2185263906672697512L;
    @Override
    public Object get() {
        List att = (List) this.getObject();
        return att;
    }
    @Override
    public void set(Object value) {
        List att = (List) this.getObject();
        List fromList = (List) value;//准备将value的值设置到att中
        att.addAll(fromList);
    }
    @Override
    public Class<?> getPropertyClass() {
        return Object.class;
    }
}