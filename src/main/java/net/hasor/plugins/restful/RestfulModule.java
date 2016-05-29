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
package net.hasor.plugins.restful;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import net.hasor.plugins.restful.api.MappingTo;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/***
 * restful插件
 * 
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public class RestfulModule extends WebModule {
    //
    public final void loadModule(WebApiBinder apiBinder) throws Throwable {
        //
        Set<Class<?>> serviceSet = apiBinder.findClass(MappingTo.class);
        serviceSet = new HashSet<Class<?>>(serviceSet);
        serviceSet.remove(MappingTo.class);
        //
        if (serviceSet == null || serviceSet.isEmpty()) {
            logger.warn("restful -> exit , not found any @MappingTo.");
            return;
        } else {
            int count = 0;
            for (Class<?> type : serviceSet) {
                if (loadType(apiBinder, type) == true) {
                    count++;
                }
            }
            if (count > 0) {
                logger.info("restful -> init restful root filter , found {} MappingTo.", count);
                apiBinder.filter("/*").through(new RestfulFilter());
            } else {
                logger.warn("restful -> exit , not add any @MappingTo.");
            }
        }
    }
    public boolean loadType(WebApiBinder apiBinder, Class<?> clazz) {
        int modifier = clazz.getModifiers();
        if (checkIn(modifier, Modifier.INTERFACE) || checkIn(modifier, Modifier.ABSTRACT)) {
            return false;
        }
        //
        if (clazz.isAnnotationPresent(MappingTo.class) == false) {
            return false;
        }
        //
        MappingTo mto = clazz.getAnnotation(MappingTo.class);
        logger.info("restful -> type ‘{}’ mappingTo: ‘{}’.", clazz.getName(), mto.value());
        MappingToDefine define = new MappingToDefine(clazz);
        apiBinder.bindType(MappingToDefine.class).uniqueName().toInstance(define);
        return true;
    }
    //
    /** 通过位运算决定check是否在data里。 */
    private boolean checkIn(final int data, final int check) {
        int or = data | check;
        return or == data;
    }
}