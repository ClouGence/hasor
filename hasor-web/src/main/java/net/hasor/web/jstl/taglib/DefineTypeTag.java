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
import net.hasor.utils.ClassUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

/**
 *
 * @version : 2013-12-24
 * @author 赵永春 (zyc@hasor.net)
 */
public class DefineTypeTag extends AbstractTag {
    private static final long serialVersionUID = 7146544912135244582L;

    @Override
    public int doStartTag() throws JspException {
        verifyAttribute(AttributeNames.Var, AttributeNames.BindType);
        //
        try {
            AppContext appContext = getAppContext();
            ClassLoader classLoader = ClassUtils.getClassLoader(appContext.getClassLoader());
            Class<?> defineType = Class.forName(this.getBindType(), false, classLoader);
            storeToVar(appContext.getInstance(defineType));
            return Tag.SKIP_BODY;
        } catch (ClassNotFoundException e) {
            throw new JspException(e);
        }
    }
}