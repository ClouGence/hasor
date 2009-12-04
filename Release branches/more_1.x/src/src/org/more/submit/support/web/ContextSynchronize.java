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
package org.more.submit.support.web;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.ServletContext;
import org.more.util.attribute.IAttribute;
/**
 * 负责SubmitContext与ServletContext进行数据同步的类。
 * Date : 2009-12-4
 * @author 赵永春
 */
class ContextSynchronize implements IAttribute {
    //========================================================================================Field
    private ServletContext context;
    //==================================================================================Constructor
    public ContextSynchronize(ServletContext context) {
        this.context = context;
    }
    //==========================================================================================Job
    @Override
    public void clearAttribute() {
        String[] ns = this.getAttributeNames();
        for (int i = 0; i < ns.length; i++)
            this.removeAttribute(ns[i]);
    }
    @Override
    public String[] getAttributeNames() {
        ArrayList<String> al = new ArrayList<String>(0);
        Enumeration<?> attEnum = this.context.getAttributeNames();
        while (attEnum.hasMoreElements())
            al.add(attEnum.nextElement().toString());
        //
        String[] ns = new String[al.size()];
        al.toArray(ns);
        return ns;
    }
    @Override
    public boolean contains(String name) {
        return (this.context.getAttribute(name) == null) ? false : true;
    }
    @Override
    public Object getAttribute(String name) {
        return this.context.getAttribute(name);
    }
    @Override
    public void removeAttribute(String name) {
        this.context.removeAttribute(name);
    }
    @Override
    public void setAttribute(String name, Object value) {
        this.context.setAttribute(name, value);
    }
}