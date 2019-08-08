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
package net.hasor.web.jstl.taglib;
import net.hasor.core.AppContext;
import net.hasor.utils.StringUtils;
import net.hasor.web.startup.RuntimeListener;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 *
 * @version : 2013-12-23
 * @author 赵永春 (zyc@hasor.net)
 */
public abstract class AbstractTag extends TagSupport {
    private static final long   serialVersionUID = 954597728447849929L;
    private              String var              = null;
    private              String beanID           = null;
    private              String name             = null;
    private              String bindType         = null;

    public String getVar() {
        return this.var;
    }

    public void setVar(final String var) {
        this.var = var;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getBindType() {
        return this.bindType;
    }

    public void setBindType(final String bindType) {
        this.bindType = bindType;
    }

    public String getBeanID() {
        return beanID;
    }

    public void setBeanID(String beanID) {
        this.beanID = beanID;
    }

    public void release() {
        this.var = null;
        this.name = null;
        this.bindType = null;
    }

    protected void verifyAttribute(AttributeNames... attrArrays) {
        for (AttributeNames attr : attrArrays) {
            if (AttributeNames.Var == attr && StringUtils.isBlank(this.var)) {
                throw new NullPointerException("tag param var is null.");
            }
            if (AttributeNames.BeanID == attr && StringUtils.isBlank(this.beanID)) {
                throw new NullPointerException("tag param beanID is null.");
            }
            if (AttributeNames.Name == attr && StringUtils.isBlank(this.name)) {
                throw new NullPointerException("tag param name is null.");
            }
            if (AttributeNames.BindType == attr && StringUtils.isBlank(this.bindType)) {
                throw new NullPointerException("tag param bindType is null.");
            }
        }
    }

    protected AppContext getAppContext() {
        ServletContext sc = this.pageContext.getServletContext();
        AppContext appContext = RuntimeListener.getAppContext(sc);
        if (appContext != null) {
            return appContext;
        }
        throw new NullPointerException("AppContext is undefined.");
    }

    protected void storeToVar(Object targetBean) {
        this.pageContext.setAttribute(this.getVar(), targetBean);
    }

    @Override
    public abstract int doStartTag() throws JspException;
}