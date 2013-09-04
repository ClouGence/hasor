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
package net.hasor.freemarker.support;
import java.lang.reflect.Method;
import net.hasor.context.AppContext;
import com.google.inject.Provider;
import freemarker.template.TemplateMethodModel;
/**
 * 
 * @version : 2013-5-24
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class FmMethodDefinition implements Provider<TemplateMethodModel> {
    private String               funName      = null;
    private Method               fmMethodType = null;
    private AppContext           appContext   = null;
    private InternalMethodObject funObject    = null;
    //
    public FmMethodDefinition(String funName, Method fmMethodType) {
        this.funName = funName;
        this.fmMethodType = fmMethodType;
    }
    public void initAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    public String getName() {
        return this.funName;
    }
    public TemplateMethodModel get() {
        if (this.funObject == null) {
            Class<?> fmMethodTargetClass = this.fmMethodType.getDeclaringClass();
            String beanName = this.appContext.getBeanName(fmMethodTargetClass);
            Object target = null;
            if (beanName != null)
                target = this.appContext.getBean(beanName);
            else
                target = this.appContext.getInstance(fmMethodTargetClass);
            //
            this.funObject = new InternalMethodObject(this.fmMethodType, target);
        }
        return this.funObject;
    }
}