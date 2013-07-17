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
package org.hasor.freemarker.support;
import org.hasor.context.AppContext;
import org.hasor.freemarker.Tag;
import com.google.inject.Provider;
/**
 * 
 * @version : 2013-5-24
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class FmTagDefinition implements Provider<InternalTagObject> {
    private String            tagName      = null;
    private Class<Tag>        fmMethodType = null;
    private AppContext        appContext   = null;
    private InternalTagObject tagObject    = null;
    //
    public FmTagDefinition(String tagName, Class<Tag> fmMethodType) {
        this.tagName = tagName;
        this.fmMethodType = fmMethodType;
    }
    public void initAppContext(AppContext appContext) {
        this.appContext = appContext;
    }
    public String getName() {
        return this.tagName;
    }
    @Override
    public InternalTagObject get() {
        if (this.tagObject == null)
            this.tagObject = new InternalTagObject(this.appContext.getInstance(this.fmMethodType));
        return this.tagObject;
    }
}