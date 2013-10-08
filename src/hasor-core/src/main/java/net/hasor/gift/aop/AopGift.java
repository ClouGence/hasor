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
package net.hasor.gift.aop;
import static net.hasor.core.context.AbstractAppContext.ContextEvent_Start;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.gift.Gift;
import net.hasor.core.gift.GiftFace;
import net.hasor.gift.aop.matchers.AopMatchers;
import com.google.inject.matcher.Matcher;
/**
 * 提供 <code>@Aop</code>注解 功能支持。
 * @version : 2013-9-13
 * @author 赵永春 (zyc@byshell.org)
 */
@Gift
public class AopGift implements GiftFace, GetContext, EventListener {
    public void loadGift(ApiBinder apiBinder) {
        Matcher<Object> matcher = AopMatchers.annotatedWith(Aop.class);//
        apiBinder.getGuiceBinder().bindInterceptor(matcher, matcher, new AopInterceptor(this));
        apiBinder.getEnvironment().getEventManager().pushEventListener(ContextEvent_Start, this);
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