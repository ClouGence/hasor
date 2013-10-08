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
package net.hasor.gift.result;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.hasor.Hasor;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.gift.Gift;
import net.hasor.core.gift.GiftFace;
import net.hasor.gift.aop.matchers.AopMatchers;
import net.hasor.web.controller.AbstractController;
import net.hasor.web.controller.Controller;
import net.hasor.web.restful.RestfulService;
import com.google.inject.matcher.Matcher;
/**
 * 
 * @version : 2013-9-26
 * @author 赵永春 (zyc@byshell.org)
 */
@Gift
public class ResultGift implements GiftFace, GetContext, EventListener {
    public void loadGift(ApiBinder apiBinder) {
        Map<Class<?>, Class<ResultProcess>> defineMap = new HashMap<Class<?>, Class<ResultProcess>>();
        //1.获取
        Set<Class<?>> resultDefineSet = apiBinder.getClassSet(ResultDefine.class);
        if (resultDefineSet == null)
            return;
        //2.注册服务
        for (Class<?> resultDefineType : resultDefineSet) {
            if (ResultProcess.class.isAssignableFrom(resultDefineType) == false) {
                Hasor.warning("loadResultDefine : not implemented ResultProcess. class=%s", resultDefineType);
                continue;
            }
            ResultDefine resultDefineAnno = resultDefineType.getAnnotation(ResultDefine.class);
            Class<ResultProcess> defineType = (Class<ResultProcess>) resultDefineType;
            Class<?> resultType = resultDefineAnno.value();
            Hasor.info("loadResultDefine annoType is %s toInstance %s", resultType, resultDefineType);
            defineMap.put(resultType, defineType);
        }
        apiBinder.getEnvironment().getEventManager().pushEventListener(AppContext.ContextEvent_Start, this);
        /*所有继承 AbstractController 并且标记了 @Controller 注解的类都是控制器*/
        Matcher<Class> matcherController = AopMatchers.subclassesOf(AbstractController.class).and(AopMatchers.annotatedWith(Controller.class));
        ResultCaller_Controller caller_1 = new ResultCaller_Controller(this, defineMap);
        apiBinder.getGuiceBinder().bindInterceptor(matcherController, AopMatchers.any(), caller_1);
        /*所有标记了 @RestfulService 注解的类都是Restful服务*/
        ResultCaller_Restful caller_2 = new ResultCaller_Restful(this, defineMap);
        apiBinder.getGuiceBinder().bindInterceptor(AopMatchers.annotatedWith(RestfulService.class), AopMatchers.any(), caller_2);
    }
    //
    private AppContext appContext = null;
    public AppContext getAppContext() {
        return this.appContext;
    }
    public void onEvent(String event, Object[] params) throws Throwable {
        this.appContext = (AppContext) params[0];
    }
}