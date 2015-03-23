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
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.mvc.ModelController;
import net.hasor.mvc.ResultProcess;
import net.hasor.mvc.api.MappingTo;
import net.hasor.mvc.result.support.ResultDefine;
import org.more.logger.LoggerHelper;
/***
 * 创建MVC环境
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class LoadHellper {
    LoadHellper() {}
    protected abstract ControllerModule module();
    protected abstract ApiBinder apiBinder();
    //
    //
    public void loadResultProcess(Class<? extends Annotation> annoType, Class<? extends ResultProcess> processType) {
        ApiBinder apiBinder = apiBinder();
        //
        LoggerHelper.logInfo("loadResultDefine annoType is %s toInstance %s", annoType, processType);
        //
        BindInfo<ResultProcess> processBindInfo = apiBinder.bindType(ResultProcess.class).uniqueName()//
                .to(processType).asEagerSingleton().toInfo();/*单例*/
        //
        ResultDefine define = apiBinder.autoAware(new ResultDefine(annoType, processBindInfo));
        apiBinder.bindType(ResultDefine.class).uniqueName().toInstance(define);
    }
    //
    public void loadType(Class<? extends ModelController> clazz) {
        int modifier = clazz.getModifiers();
        if (checkIn(modifier, Modifier.INTERFACE) || checkIn(modifier, Modifier.ABSTRACT)) {
            return;
        }
        //
        String newID = UUID.randomUUID().toString();
        boolean hasMapping = false;
        ApiBinder apiBinder = apiBinder();
        //
        Method[] methodArrays = clazz.getMethods();
        for (Method atMethod : methodArrays) {
            if (atMethod.isAnnotationPresent(MappingTo.class) == false) {
                continue;
            }
            hasMapping = true;
            //
            MappingTo mto = atMethod.getAnnotation(MappingTo.class);
            LoggerHelper.logInfo("method ‘%s’ mappingTo: ‘%s’, form Type :%s.", atMethod.getName(), mto.value(), clazz.getName());
            MappingDefine define = module().createMappingDefine(newID, atMethod);
            apiBinder.bindType(MappingDefine.class).uniqueName().toInstance(define);
        }
        //
        if (hasMapping == true) {
            apiBinder.bindType(clazz).idWith(newID);
        }
    }
    //
    /**通过位运算决定check是否在data里。*/
    private static boolean checkIn(final int data, final int check) {
        int or = data | check;
        return or == data;
    }
}