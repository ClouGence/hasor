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
package org.hasor.test.events;
import org.hasor.context.AppContext;
import org.hasor.context.HasorEventListener;
import org.hasor.context.Lifecycle;
import org.hasor.test.AbstractTestContext;
import org.junit.Test;
/**
 * 
 * @version : 2013-7-16
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class EventTest extends AbstractTestContext {
    @Override
    protected void initContext(AppContext appContext) {
        HasorEventListener event = new HasorEventListener() {
            @Override
            public void onEvent(String event, Object[] params) {
                System.out.println(event + "\t begin   " + params[0]);
                try {
                    Thread.sleep(1100);
                } catch (InterruptedException e) {}
                System.out.println(event + "\t end   " + params[0]);
            }
        };
        appContext.getEventManager().addEventListener(Lifecycle.PhaseEvent_Init, event);
        appContext.getEventManager().addEventListener(Lifecycle.PhaseEvent_Start, event);
        appContext.getEventManager().addEventListener(Lifecycle.PhaseEvent_Stop, event);
        appContext.getEventManager().addEventListener(Lifecycle.PhaseEvent_Destroy, event);
        //
        appContext.getEventManager().addEventListener("EE", event);
    }
    @Test
    public void phaseEvent() {
        this.getAppContext().start();
        int i = 0;
        while (true) {
            this.getAppContext().getEventManager().doAsynEvent("EE", i++);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
        //        this.getAppContext().stop();
        //        this.getAppContext().destroy();
    }
}