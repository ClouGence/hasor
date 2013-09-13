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
package net.hasor.core.gift.before;
import static net.hasor.core.context.AbstractAppContext.ContextEvent_Start;
import net.hasor.core.ApiBinder;
import net.hasor.core.AppContext;
import net.hasor.core.EventListener;
import net.hasor.core.anno.guice.AopMatchers;
import net.hasor.core.gift.InitGift;
import com.google.inject.matcher.Matcher;
/**
 * 
 * @version : 2013-9-13
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class BeforeGift implements InitGift, GetContext, EventListener {
    private AppContext appContext = null;
    //
    //
    public void loadGift(ApiBinder apiBinder) {
        Matcher<Object> matcher = AopMatchers.annotatedWith(Before.class);//
        apiBinder.getGuiceBinder().bindInterceptor(matcher, matcher, new BeforeInterceptor(this));
        apiBinder.getEnvironment().getEventManager().pushEventListener(ContextEvent_Start, this);
    }
    //
    public void onEvent(String event, Object[] params) throws Throwable {
        this.appContext = (AppContext) params[0];
    }
    public AppContext getAppContext() {
        return this.appContext;
    }
}