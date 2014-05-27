/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
 * @author 赵永春(zyc@hasor.net)
 */
public class DefineBean_Tag extends AbstractHasorTag {
    private static final long serialVersionUID = 8066383523368039588L;
    private String            var              = null;
    private String            bean             = null;
    public String getVar() {
        return this.var;
    }
    public void setVar(String var) {
        this.var = var;
    }
    public String getBean() {
        return this.bean;
    }
    public void setBean(String bean) {
        this.bean = bean;
    }
    //
    //
    //
    public void release() {
        this.var = null;
        this.bean = null;
    }
    public int doStartTag() throws JspException {
        if (StringUtils.isBlank(this.var))
            throw new NullPointerException("tag param var is null.");
        if (StringUtils.isBlank(this.bean))
            throw new NullPointerException("tag param bean is null.");
        //
        Object targetBean = Functions.defineBean(this.bean);
        this.pageContext.setAttribute(var, targetBean);
        return SKIP_BODY;
    }
}