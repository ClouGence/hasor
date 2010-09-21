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
import java.util.Iterator;
import java.util.Map;
import org.more.core.copybean.BeanType;
import org.more.core.copybean.PropertyReaderWrite;
/**
 * Map类读写器。使用该类作为读写器可以实现从Map对象中拷贝属性或者向Map中拷贝属性。
 * @version 2009-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class MapRW extends BeanType {
    /**  */
    private static final long serialVersionUID = -3987939968587042522L;
    public boolean checkObject(Object object) {
        return object instanceof Map;
    }
    protected Iterator<String> iteratorNames(Object obj) {
        Map map = (Map) obj;
        return map.keySet().iterator();
    }
    protected PropertyReaderWrite getPropertyRW(Object obj, String name) {
        MapReaderWrite prw = new MapReaderWrite();
        prw.setName(name);
        prw.setObject(obj);
        return prw;
    }
}
/**
 * Map类型的属性读写器
 * Date : 2009-5-21
 * @author 赵永春
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
class MapReaderWrite extends PropertyReaderWrite {
    /**  */
    private static final long serialVersionUID = 2185263906672697512L;
    public Object get() {
        Map att = (Map) this.getObject();
        return att.get(this.getName());
    }
    public void set(Object value) {
        Map att = (Map) this.getObject();
        att.put(this.getName(), value);
    }
    public Class<?> getPropertyClass() {
        return Object.class;
    }
}