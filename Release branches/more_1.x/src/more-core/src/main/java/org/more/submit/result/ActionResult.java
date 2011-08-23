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
package org.more.submit.result;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.more.submit.impl.DefaultResultImpl;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * 在返回action之前调用另外一个action
 * @version : 2011-7-25
 * @author 赵永春 (zyc@byshell.org)
 */
public class ActionResult extends DefaultResultImpl<URI> implements IAttribute {
    private IAttribute att = new AttBase();
    /*----------------------------------------------------------------*/
    public ActionResult(String schema, String actionPath) throws URISyntaxException {
        super("submitAction", new URI(schema + "://" + actionPath));
    }
    public ActionResult(String actionPath) throws URISyntaxException {
        super("submitAction", new URI(actionPath));
    }
    public ActionResult(URI actionPath) {
        super("submitAction", actionPath);
    }
    /*----------------------------------------------------------------*/
    /**获取要执行的Action地址。*/
    public URI getActionURI() {
        return this.getReturnValue();
    }
    public boolean contains(String name) {
        return this.att.contains(name);
    }
    public void setAttribute(String name, Object value) {
        this.att.setAttribute(name, value);
    }
    public Object getAttribute(String name) {
        return this.att.getAttribute(name);
    }
    public void removeAttribute(String name) {
        this.att.removeAttribute(name);
    }
    public String[] getAttributeNames() {
        return this.att.getAttributeNames();
    }
    public void clearAttribute() {
        this.att.clearAttribute();
    }
    public Map<String, Object> toMap() {
        return this.att.toMap();
    }
}