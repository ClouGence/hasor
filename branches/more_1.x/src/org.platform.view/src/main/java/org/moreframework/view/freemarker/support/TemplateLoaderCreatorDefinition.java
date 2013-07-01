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
package org.moreframework.view.freemarker.support;
import org.moreframework.context.AppContext;
import org.moreframework.view.freemarker.FmTemplateLoaderCreator;
import com.google.inject.Provider;
/**
 * 声明一个Cache，该Cache需要实现{@link ICache}接口。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
class TemplateLoaderCreatorDefinition implements Provider<FmTemplateLoaderCreator> {
    private String                         name                = null;
    private AppContext                     appContext          = null;
    private Class<FmTemplateLoaderCreator> loaderCreatorType   = null;
    private FmTemplateLoaderCreator        loaderCreatorObject = null;
    //
    public TemplateLoaderCreatorDefinition(String name, Class<FmTemplateLoaderCreator> loaderCreatorType) {
        this.name = name;
        this.loaderCreatorType = loaderCreatorType;
    }
    public String getName() {
        return name;
    }
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    @Override
    public FmTemplateLoaderCreator get() {
        if (this.loaderCreatorObject == null)
            this.loaderCreatorObject = this.appContext.getInstance(this.loaderCreatorType);
        return this.loaderCreatorObject;
    }
}