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
package net.hasor.mvc.support;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.Module;
import net.hasor.mvc.MappingTo;
import net.hasor.mvc.ModelController;
/***
 * 创建MVC环境
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public class ControllerModule implements Module {
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        //1.搜索ModelController
        Set<Class<?>> controllerSet = apiBinder.findClass(ModelController.class);
        if (controllerSet == null || controllerSet.isEmpty() == true) {
            return;
        }
        //2.绑定到Hasor
        for (Class<?> clazz : controllerSet) {
            String newID = UUID.randomUUID().toString();
            boolean hasMapping = false;
            //
            Method[] methodArrays = clazz.getMethods();
            for (Method atMethod : methodArrays) {
                if (atMethod.isAnnotationPresent(MappingTo.class) == false) {
                    continue;
                }
                hasMapping = true;
                apiBinder.bindType(MappingDefine.class).uniqueName().toInstance(new MappingDefine(newID, atMethod));
            }
            //
            if (hasMapping == true) {
                apiBinder.bindType(clazz).idWith(newID);
            }
        }
        //3.安装服务
        RootController root = new RootController();
        apiBinder.pushListener(AppContext.ContextEvent_Started, root);
        apiBinder.bindType(RootController.class).toInstance(root);
    }
}