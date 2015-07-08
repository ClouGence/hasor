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
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.hasor.core.EventContext;
import net.hasor.core.Hasor;
import net.hasor.core.XmlNode;
import net.hasor.mvc.ModelController;
import net.hasor.mvc.ResultProcess;
import net.hasor.mvc.support.params.ParamCallInterceptor;
import net.hasor.mvc.support.result.ResultCallInterceptor;
import net.hasor.mvc.support.valid.ValidationCallInterceptor;
import net.hasor.web.WebApiBinder;
import net.hasor.web.WebModule;
import org.more.util.ClassUtils;
/***
 * 创建MVC环境
 * @version : 2014-1-13
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class ControllerModule extends WebModule {
    private static AtomicBoolean initController = new AtomicBoolean(false);
    //
    public void loadModule(final WebApiBinder apiBinder) throws Throwable {
        logger.info("work at ControllerModule. -> {}", this.getClass());
        //1.create LoadHellper
        LoadHellper helper = new LoadHellper() {
            public WebApiBinder apiBinder() {
                return apiBinder;
            }
            protected ControllerModule module() {
                return ControllerModule.this;
            }
        };
        //2.install-避免初始化多次
        if (initController.compareAndSet(false, true)) {
            List<XmlNode> allResultProcess = apiBinder.getEnvironment().getSettings().merageXmlNode("hasor.mvcConfig", "resultProces");
            for (XmlNode atNode : allResultProcess) {
                String annoTypeStr = atNode.getAttribute("annoType");
                String processTypeStr = atNode.getAttribute("processType");
                try {
                    ClassLoader loader = Thread.currentThread().getContextClassLoader();
                    Class<? extends Annotation> annoType = (Class<? extends Annotation>) ClassUtils.getClass(loader, annoTypeStr);
                    Class<? extends ResultProcess> processType = (Class<? extends ResultProcess>) ClassUtils.getClass(loader, processTypeStr);
                    //
                    helper.loadResultProcess(annoType, processType);
                    logger.info("resultProcess: anno[{}] to:{}", annoTypeStr, processTypeStr);
                } catch (Exception e) {
                    logger.error("resultProcess: anno[{}] to:{} ,error -> {}", annoTypeStr, processTypeStr, e);
                }
            }
            //内置插件
            helper.loadInterceptor(ParamCallInterceptor.class);
            helper.loadInterceptor(ValidationCallInterceptor.class);
            helper.loadInterceptor(ResultCallInterceptor.class);
            //
            //框架初始化
            EventContext env = apiBinder.getEnvironment().getEventContext();
            RequestScope scope = Hasor.pushStartListener(env, new RequestScope());
            RootController rootController = Hasor.pushStartListener(env, new RootController());
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
}