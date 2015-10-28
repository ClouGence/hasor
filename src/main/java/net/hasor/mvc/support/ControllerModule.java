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
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;
import net.hasor.core.ApiBinder;
import net.hasor.core.Environment;
import net.hasor.core.Hasor;
import net.hasor.core.ApiBinder.MetaDataBindingBuilder;
import net.hasor.mvc.ModelController;
import net.hasor.mvc.WebCallInterceptor;
import net.hasor.mvc.api.MappingTo;
import net.hasor.mvc.resful.ResfulCallInterceptor;
import net.hasor.mvc.scope.RequestScope;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
/***
 * 创建MVC环境
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class ControllerModule extends WebModule {
    private static AtomicBoolean initController = new AtomicBoolean(false);
    //
    public final void loadModule(final WebApiBinder apiBinder) throws Throwable {
        logger.info("work at ControllerModule. -> {}", this.getClass());
        //1.create LoadHellper
        //2.install-避免初始化多次
        if (initController.compareAndSet(false, true)) {
            //内置插件
            helper.loadInterceptor(ResfulCallInterceptor.class);
            //
            //框架初始化
            Environment env = apiBinder.getEnvironment();
            RequestScope scope = Hasor.autoAware(env, new RequestScope());
            RootController rootController = Hasor.autoAware(env, new RootController());
            apiBinder.bindType(RequestScope.class).toInstance(scope);
            apiBinder.bindType(RootController.class).toInstance(rootController);
            //
            apiBinder.filter("/*").through(scope);
            apiBinder.filter("/*").through(new ControllerFilter());
            //
            apiBinder.bindType(ContextMap.class).toScope(scope);
        }
        //
        //3.load config
        this.loadController(helper);
    }
    //
    protected abstract void loadController(LoadHellper helper) throws Throwable;
    /**
     * 创建 {@link MappingInfoDefine}
     * @param clazz 目标控制器类型。
     * @return 返回mvc定义。
     */
    public MappingInfoDefine createMappingDefine(Class<? extends ModelController> clazz) {
        return new MappingInfoDefine(clazz);
    }
    //
    /**装载拦截器*/
    public void loadInterceptor(WebApiBinder apiBinder) {
        logger.info("loadInterceptor type is {}", interceptor);
        Environment env = this.apiBinder().getEnvironment();
        //
        MetaDataBindingBuilder<WebCallInterceptor> metaDatainfo = this.apiBinder().bindType(WebCallInterceptor.class).uniqueName().to(interceptor);
        WebCallInterceptorDefine define = Hasor.autoAware(env, new WebCallInterceptorDefine(metaDatainfo.toInfo()));
        apiBinder().bindType(WebCallInterceptorDefine.class).uniqueName().toInstance(define);
    }
    //
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
    //
    /**通过位运算决定check是否在data里。*/
    private static boolean checkIn(final int data, final int check) {
        int or = data | check;
        return or == data;
    }
}