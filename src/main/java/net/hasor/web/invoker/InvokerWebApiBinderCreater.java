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
package net.hasor.web.invoker;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.core.Environment;
import net.hasor.core.binder.ApiBinderCreater;
import net.hasor.web.MimeType;
import net.hasor.web.ServletVersion;
import net.hasor.web.WebApiBinder;
import net.hasor.web.annotation.MappingTo;
import net.hasor.web.listener.ListenerPipeline;
import net.hasor.web.listener.ManagedListenerPipeline;
import org.more.util.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * 渲染插件，的ApiBinder扩展器。
 * 让 {@link ApiBinder} 支持 {@link WebApiBinder} 类型
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class InvokerWebApiBinderCreater implements ApiBinderCreater {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public WebApiBinder createBinder(final ApiBinder apiBinder) throws IOException, XMLStreamException {
        Environment environment = apiBinder.getEnvironment();
        Object context = environment.getContext();
        if (!(context instanceof ServletContext)) {
            return null;
        }
        ServletContext servletContext = (ServletContext) context;
        //
        // .MimeType
        MimeTypeSupplier mimeTypeContext = new MimeTypeSupplier(servletContext);
        mimeTypeContext.loadStream("/META-INF/mime.types.xml");
        mimeTypeContext.loadStream("mime.types.xml");
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
        ManagedListenerPipeline lPipline = new ManagedListenerPipeline();
        //
        // .Binder
        apiBinder.bindType(ServletContext.class).toInstance(servletContext);
        apiBinder.bindType(ServletVersion.class).toInstance(curVersion);
        apiBinder.bindType(ListenerPipeline.class).toInstance(lPipline);
        //
        // .MappingTo
        Set<Class<?>> serviceSet = apiBinder.findClass(MappingTo.class);
        serviceSet = (serviceSet == null) ? new HashSet<Class<?>>() : new HashSet<Class<?>>(serviceSet);
        serviceSet.remove(MappingTo.class);
        if (serviceSet.isEmpty()) {
            logger.warn("restful -> exit , not found any @MappingTo.");
        }
        int count = 0;
        for (Class<?> type : serviceSet) {
            if (loadType(apiBinder, type)) {
                count++;
            }
        }
        if (count > 0) {
            logger.info("restful -> init restful root filter , found {} MappingTo.", count);
        } else {
            logger.warn("restful -> exit , not add any @MappingTo.");
        }
        //
        // .WebApiBinder
        return new InvokerWebApiBinder(curVersion, mimeTypeContext, apiBinder);
    }
    public boolean loadType(ApiBinder apiBinder, Class<?> clazz) {
        int modifier = clazz.getModifiers();
        if (checkIn(modifier, Modifier.INTERFACE) || checkIn(modifier, Modifier.ABSTRACT)) {
            return false;
        }
        if (!clazz.isAnnotationPresent(MappingTo.class)) {
            return false;
        }
        //
        MappingTo mto = clazz.getAnnotation(MappingTo.class);
        logger.info("restful -> type ‘{}’ mappingTo: ‘{}’.", clazz.getName(), mto.value());
        List<Method> methodList = BeanUtils.getMethods(clazz);
        BindInfo<?> bindInfo = apiBinder.bindType(clazz).uniqueName().toInfo();
        InMappingDef define = new InMappingDef(0, bindInfo, mto.value(), methodList, false);
        apiBinder.bindType(InMappingDef.class).uniqueName().toInstance(define);
        return true;
    }
    //
    /** 通过位运算决定check是否在data里。 */
    private boolean checkIn(final int data, final int check) {
        int or = data | check;
        return or == data;
    }
}