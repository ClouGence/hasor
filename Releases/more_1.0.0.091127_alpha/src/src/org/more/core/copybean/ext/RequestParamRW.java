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
import javax.servlet.ServletRequest;
import org.more.core.copybean.BeanType;
import org.more.core.copybean.PropertyReaderWrite;
/**
 * request类读写器。使用该类作为读写器可以实现从request中拷贝属性或者向request中拷贝属性。
 * Date : 2009-5-15
 * @author 赵永春
 */
@SuppressWarnings("unchecked")
public class RequestParamRW extends BeanType {
    /**  */
    private static final long serialVersionUID = 6914594767047640104L;
    @Override
    public boolean checkObject(Object object) {
        return object instanceof ServletRequest;
    }
    @Override
    protected Iterator<String> iteratorNames(Object obj) {
        ServletRequest request = (ServletRequest) obj;
        return request.getParameterMap().keySet().iterator();
    }
    @Override
    protected PropertyReaderWrite getPropertyRW(Object obj, String name) {
        RequestParamReaderWrite prw = new RequestParamReaderWrite();
        prw.setName(name);
        prw.setObject(obj);
        return prw;
    }
}
/**
 * request类型的属性读写器
 * Date : 2009-5-21
 * @author 赵永春
 */
class RequestParamReaderWrite extends PropertyReaderWrite {
    /**  */
    private static final long serialVersionUID = 4150884799794227003L;
    @Override
    public Object get() {
        ServletRequest req = (ServletRequest) this.getObject();
        String[] ns = req.getParameterValues(this.getName());
        if (ns == null || ns.length == 0)
            return null;
        else if (ns.length == 1)
            return ns[0];
        else
            return ns;
    }
    @Override
    public void set(Object value) {}
    @Override
    public boolean canWrite() {
        return false;
    }
    @Override
    public Class<?> getPropertyClass() {
        return String.class;
    }
}