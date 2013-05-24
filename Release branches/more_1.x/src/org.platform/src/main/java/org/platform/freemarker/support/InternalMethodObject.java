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
package org.platform.freemarker.support;
import java.util.List;
import org.platform.Assert;
import org.platform.context.AppContext;
import org.platform.freemarker.IFmMethod;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
/**
 * ±Í«©∂‘œÛ°£
 * @version : 2012-5-13
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class InternalMethodObject implements TemplateMethodModel {
    private IFmMethod  methodBody = null;
    private AppContext appContext = null;
    public InternalMethodObject(IFmMethod methodBody, AppContext appContext) {
        this.methodBody = methodBody;
        this.appContext = appContext;
        Assert.isNotNull(methodBody, "method Object is null.");
    }
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        return this.methodBody.callMethod(arguments.toArray(), this.appContext);
    }
}