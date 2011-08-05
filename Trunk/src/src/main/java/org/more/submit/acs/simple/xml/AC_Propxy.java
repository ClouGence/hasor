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
package org.more.submit.acs.simple.xml;
import java.net.URI;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.submit.ActionContext;
import org.more.submit.ActionInvoke;
import org.more.submit.ActionPackage;
/**
 * ¥˙¿ÌAC
 * @version : 2011-7-25
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class AC_Propxy implements ActionContext {
    private AbstractApplicationContext context = null;
    private String                     refBean = null;
    //
    private ActionContext              ac      = null;
    //
    public AC_Propxy(AbstractApplicationContext context, String refBean) {
        this.context = context;
        this.refBean = refBean;
    }
    //
    public ActionContext getAC() throws Throwable {
        if (ac == null)
            this.ac = this.context.getBean(refBean);
        return this.ac;
    };
    public ActionInvoke getAction(URI uri) throws Throwable {
        return this.getAC().getAction(uri);
    }
    public ActionPackage definePackage(String packageName) throws Throwable {
        return this.getAC().definePackage(packageName);
    }
};