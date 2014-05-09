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
import net.hasor.web.jstl.tagfun.Functions;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-12-24
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
public class DefineType_Tag extends AbstractHasorTag {
    private static final long serialVersionUID = 7146544912135244582L;
    private String            var              = null;
    private String            type             = null;
    public String getVar() {
        return this.var;
    }
    public void setVar(String var) {
        this.var = var;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    //
    //
    //
    public void release() {
        this.var = null;
        this.type = null;
    }
    public int doStartTag() throws JspException {
        if (StringUtils.isBlank(this.var))
            throw new NullPointerException("tag param var is null.");
        if (StringUtils.isBlank(this.type))
            throw new NullPointerException("tag param type is null.");
        //
        try {
            Object targetBean = Functions.defineType(type);
            this.pageContext.setAttribute(var, targetBean);
            return SKIP_BODY;
        } catch (ClassNotFoundException e) {
            throw new JspException(e);
        }
    }
}