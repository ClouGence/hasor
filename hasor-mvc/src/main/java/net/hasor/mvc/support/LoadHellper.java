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
import java.lang.reflect.Modifier;
import net.hasor.core.ApiBinder;
import net.hasor.core.BindInfo;
import net.hasor.mvc.ModelController;
import net.hasor.mvc.ResultProcess;
import net.hasor.mvc.WebCallInterceptor;
import net.hasor.mvc.api.MappingTo;
import net.hasor.mvc.support.inner.ResultProcessDefine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/***
 * 创建MVC环境
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class LoadHellper {
    protected static Logger logger = LoggerFactory.getLogger(LoadHellper.class);
    protected abstract ControllerModule module();
    public abstract ApiBinder apiBinder();
    //
    /**结果处理器*/
    public void loadResultProcess(Class<? extends Annotation> annoType, Class<? extends ResultProcess> processType) {
        logger.info("loadResultDefine annoType is {} toInstance {}", annoType, processType);
        //
        BindInfo<ResultProcess> info = this.apiBinder().bindType(ResultProcess.class).uniqueName().to(processType).toInfo();
        ResultProcessDefine define = new ResultProcessDefine(annoType, info);
        apiBinder().autoAware(define);
        apiBinder().bindType(ResultProcessDefine.class).uniqueName().toInstance(define);
    }
    //
    /**装载拦截器*/
    public void loadInterceptor(Class<? extends WebCallInterceptor> interceptor) {
        logger.info("loadInterceptor type is {}", interceptor);
        BindInfo<WebCallInterceptor> info = this.apiBinder().bindType(WebCallInterceptor.class).uniqueName().to(interceptor).toInfo();
        WebCallInterceptorDefine define = new WebCallInterceptorDefine(info);
        apiBinder().autoAware(define);
        apiBinder().bindType(WebCallInterceptorDefine.class).uniqueName().toInstance(define);
    }
    /**装载控制器*/
    public void loadType(Class<? extends ModelController> clazz) {
        int modifier = clazz.getModifiers();
        if (checkIn(modifier, Modifier.INTERFACE) || checkIn(modifier, Modifier.ABSTRACT)) {
            return;
        }
        //
        ApiBinder apiBinder = apiBinder();
        if (clazz.isAnnotationPresent(MappingTo.class) == false) {
            return;
        }
        //
        MappingTo mto = clazz.getAnnotation(MappingTo.class);
        logger.info("type ‘{}’ mappingTo: ‘{}’.", clazz.getName(), mto.value());
        MappingInfoDefine define = module().createMappingDefine(clazz);
        apiBinder.bindType(MappingInfoDefine.class).uniqueName().toInstance(define);
        apiBinder.bindType(clazz);
    }
    /**通过位运算决定check是否在data里。*/
    private static boolean checkIn(final int data, final int check) {
        int or = data | check;
        return or == data;
    }
}