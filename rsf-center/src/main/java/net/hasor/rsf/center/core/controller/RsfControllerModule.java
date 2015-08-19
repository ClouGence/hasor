/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.center.core.controller;
import java.util.Set;
import net.hasor.mvc.ModelController;
import net.hasor.mvc.api.MappingTo;
import net.hasor.mvc.support.ControllerModule;
import net.hasor.mvc.support.LoadHellper;
import net.hasor.web.WebApiBinder;
/**
 * 
 * @version : 2015年8月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfControllerModule extends ControllerModule {
    protected void loadController(LoadHellper helper) throws Throwable {
        WebApiBinder apiBinder = helper.apiBinder();
        Set<Class<?>> controllerSet = apiBinder.getEnvironment().findClass(ModelController.class);
        for (Class<?> controllerType : controllerSet) {
            if (controllerType.isAnnotationPresent(MappingTo.class)) {
                helper.loadType((Class<? extends ModelController>) controllerType);
            }
        }
    }
}