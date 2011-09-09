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
 * @version 2009-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class RequestParamRW extends BeanType<ServletRequest> {
    /**  */
    private static final long serialVersionUID = 6914594767047640104L;
    public boolean checkObject(Object object) {
        return object instanceof ServletRequest;
    }
    protected Iterator<String> iteratorNames(ServletRequest obj) {
        ServletRequest request = (ServletRequest) obj;
        return request.getParameterMap().keySet().iterator();
    }
    protected PropertyReaderWrite<ServletRequest> getPropertyRW(ServletRequest obj, String name) {
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
class RequestParamReaderWrite extends PropertyReaderWrite<ServletRequest> {
    /**  */
    private static final long serialVersionUID = 4150884799794227003L;
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
    public void set(Object value) {}
    public boolean canWrite() {
        return false;
    }
    public Class<?> getPropertyClass() {
        return String.class;
    }
}