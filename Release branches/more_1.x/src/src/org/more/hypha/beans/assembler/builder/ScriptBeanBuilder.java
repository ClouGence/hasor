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
package org.more.hypha.beans.assembler.builder;
import java.lang.reflect.Method;
import org.more.core.classcode.BuilderMode;
import org.more.core.classcode.ClassEngine;
import org.more.core.classcode.MethodDelegate;
import org.more.core.error.InvokeException;
import org.more.hypha.beans.assembler.support.ScriptBean;
import org.more.hypha.beans.define.ScriptBeanDefine;
import org.more.hypha.commons.logic.AbstractBeanBuilder;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 
 * @version 2011-2-15
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ScriptBeanBuilder extends AbstractBeanBuilder<ScriptBeanDefine> {
    private static ILog log = LogFactory.getLog(ScriptBeanBuilder.class);
    /*------------------------------------------------------------------------------*/
    public Class<?> loadType(ScriptBeanDefine define, Object[] params) throws Throwable {
        ClassLoader loader = this.getApplicationContext().getBeanClassLoader();
        ClassEngine ce = new ClassEngine();
        ce.setClassName("org.more.hypha.SB." + define.getID());
        ce.setBuilderMode(BuilderMode.Super);
        MethodDelegate delegate = new ScriptMethodDelegate();
        for (String str : define.getImplementList()) {
            log.debug("append Script delegate {%0}.", str);
            ce.addDelegate(loader.loadClass(str), delegate);
        }
        return ce.builderClass().toClass();
    }
    public <O> O createBean(Class<?> classType, ScriptBeanDefine define, Object[] params) throws Throwable {
        ScriptBean cb = new ScriptBean(define);
        // TODO Auto-generated method stub
        return null;
    }
};
class ScriptMethodDelegate implements MethodDelegate {
    private static ILog log = LogFactory.getLog(ScriptMethodDelegate.class);
    public Object invoke(Method callMethod, Object target, Object[] params) throws InvokeException {
        try {
            Method m = target.getClass().getMethod(callMethod.getName(), callMethod.getParameterTypes());
            return m.invoke(target, params);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}