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
package org.more.hypha.annotation.assembler;
import org.more.hypha.DefineResource;
import org.more.hypha.annotation.AnnotationDefineResourcePlugin;
import org.more.hypha.annotation.Aop;
import org.more.hypha.annotation.Bean;
import org.more.hypha.annotation.KeepWatchParser;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.configuration.DefineResourceImpl;
/**
 * 
 * @version 2010-10-14
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class Watch_Aop implements KeepWatchParser {
    public void process(Class<?> beanType, DefineResource resource, AnnotationDefineResourcePlugin plugin) {
        DefineResourceImpl resourceImpl = (DefineResourceImpl) resource;
        Bean bean = beanType.getAnnotation(Bean.class);
        // ID
        String id = bean.id();
        if (id.equals("") == true) {
            StringBuffer idb = new StringBuffer();
            String logicPackage = bean.logicPackage();
            if (logicPackage.equals("") == true)
                logicPackage = beanType.getPackage().getName();
            idb.append(logicPackage);
            idb.append(".");
            String name = bean.name();
            if (name.equals("") == true)
                name = beanType.getSimpleName();
            idb.append(name);
            id = idb.toString();
        }
        //
        AbstractBeanDefine define = resourceImpl.getBeanDefine(id);
        Aop aop = beanType.getAnnotation(Aop.class);
        System.out.println("Ω‚ŒˆAop:" + define + "\t\t" + aop);
    }
}