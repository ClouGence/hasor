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
import org.more.submit.Result;
import org.more.util.attribute.AttBase;
import org.more.util.attribute.IAttribute;
/**
 * ftl
 * @version : 2011-7-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class SubmitActionResult extends Result<URI> implements IAttribute {
    private IAttribute att = new AttBase();
    public SubmitActionResult(String schema, String actionPath) throws URISyntaxException {
        super("submitAction", new URI(schema + "://" + actionPath));
    }
    public SubmitActionResult(String actionPath) throws URISyntaxException {
        super("submitAction", new URI(actionPath));
    }
    public SubmitActionResult(URI actionPath) {
        super("submitAction", actionPath);
    }
    /*----------------------------------------------------------------*/
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