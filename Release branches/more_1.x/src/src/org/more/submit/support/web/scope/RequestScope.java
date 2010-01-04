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
package org.more.submit.support.web.scope;
import java.util.Enumeration;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import org.more.util.attribute.IAttribute;
/**
 * 负责提供HttpServletRequest到{@link IAttribute IAttribute接口}的代理。
 * <br/>Date : 2009-12-28
 * @author 赵永春
 */
public class RequestScope implements IAttribute {
    private HttpServletRequest request;
    public RequestScope(HttpServletRequest request) {
        this.request = request;
    }
    public boolean contains(String name) {
        return this.request.getAttribute(name) != null;
    };
    public void setAttribute(String name, Object value) {
        this.request.setAttribute(name, value);
    };
    public Object getAttribute(String name) {
        return this.request.getAttribute(name);
    };
    public void removeAttribute(String name) {
        this.request.removeAttribute(name);
    };
    public String[] getAttributeNames() {
        Vector<String> v = new Vector<String>(0);
        Enumeration<?> attEnum = this.request.getAttributeNames();
        while (attEnum.hasMoreElements())
            v.add(attEnum.nextElement().toString());
        String[] ns = new String[v.size()];
        v.toArray(ns);
        return ns;
    };
    public void clearAttribute() {
        String[] ns = this.getAttributeNames();
        for (int i = 0; i < ns.length; i++)
            this.removeAttribute(ns[i]);
    };
}