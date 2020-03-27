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
package net.hasor.web.binder;
import net.hasor.core.ApiBinder;
import net.hasor.core.Environment;
import net.hasor.core.binder.ApiBinderCreator;
import net.hasor.web.MimeType;
import net.hasor.web.ServletVersion;
import net.hasor.web.WebApiBinder;
import net.hasor.web.mime.MimeTypeSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.IOException;

/**
 * 渲染插件，的ApiBinder扩展器。
 * 让 {@link ApiBinder} 支持 {@link WebApiBinder} 类型
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokerWebApiBinderCreator implements ApiBinderCreator<WebApiBinder> {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public WebApiBinder createBinder(final ApiBinder apiBinder) throws IOException {
        Environment environment = apiBinder.getEnvironment();
        Object context = environment.getContext();
        //
        try {
            apiBinder.getEnvironment().getClassLoader().loadClass("javax.servlet.ServletContext");
        } catch (ClassNotFoundException e) {
            return null;
        }
        //
        if (!(context instanceof ServletContext)) {
            return null;
        }
        return newBinder(apiBinder);
    }

    //
    public static WebApiBinder newBinder(ApiBinder apiBinder) throws IOException {
        Environment environment = apiBinder.getEnvironment();
        Object context = environment.getContext();
        if (!(context instanceof ServletContext)) {
            return null;
        }
        ServletContext servletContext = (ServletContext) context;
        //
        // .MimeType
        MimeTypeSupplier mimeTypeContext = new MimeTypeSupplier(servletContext);
        mimeTypeContext.loadResource("/META-INF/mime.types.xml");
        mimeTypeContext.loadResource("mime.types.xml");
        apiBinder.bindType(MimeType.class, mimeTypeContext);
        //
        //.ServletVersion
        ServletVersion curVersion = ServletVersion.V2_3;
        try {
            environment.getClassLoader().loadClass("javax.servlet.ServletRequestListener");
            curVersion = ServletVersion.V2_4;
            servletContext.getContextPath();
            curVersion = ServletVersion.V2_5;
            servletContext.getEffectiveMajorVersion();
            curVersion = ServletVersion.V3_0;
            servletContext.getVirtualServerName();
            curVersion = ServletVersion.V3_1;
        } catch (Throwable e) { /* 忽略 */ }
        //
        // .Binder
        apiBinder.bindType(ServletContext.class).toInstance(servletContext);
        apiBinder.bindType(ServletVersion.class).toInstance(curVersion);
        //
        return new InvokerWebApiBinder(curVersion, mimeTypeContext, apiBinder);
    }
}