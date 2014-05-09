/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.hasor.web.jstl.taglib;
import javax.servlet.jsp.JspException;
import org.more.util.StringUtils;
import net.hasor.web.jstl.tagfun.Functions;
/**
 * 
 * @version : 2013-12-24
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class DefineBinding_Tag extends AbstractHasorTag {
    private static final long serialVersionUID = -7899624524135156746L;
    private String            var              = null;
    private String            name             = null;
    private String            bindingType      = null;
    public String getVar() {
        return this.var;
    }
    public void setVar(String var) {
        this.var = var;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBindingType() {
        return bindingType;
    }
    public void setBindingType(String bindingType) {
        this.bindingType = bindingType;
    }
    //
    //
    //
    public void release() {
        this.var = null;
        this.name = null;
        this.bindingType = null;
    }
    public int doStartTag() throws JspException {
        if (StringUtils.isBlank(this.var))
            throw new NullPointerException("tag param var is null.");
        if (StringUtils.isBlank(this.name))
            throw new NullPointerException("tag param name is null.");
        if (StringUtils.isBlank(this.bindingType))
            throw new NullPointerException("tag param bindingType is null.");
        //
        try {
            Object targetBean = Functions.defineBinding(name, bindingType);
            this.pageContext.setAttribute(var, targetBean);
            return SKIP_BODY;
        } catch (ClassNotFoundException e) {
            throw new JspException(e);
        }
    }
}