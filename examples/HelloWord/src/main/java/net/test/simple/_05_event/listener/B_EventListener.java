/*
 * Copyright 2008-2009 the original ’‘”¿¥∫(zyc@hasor.net).
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
package net.test.simple._05_event.listener;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
import net.hasor.plugins.event.Listener;
/**
 *  ¬º˛º‡Ã˝∆˜B
 * @version : 2014-1-11
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
@Listener("Event_B")
public class B_EventListener implements EventListener {
    public void onEvent(String event, Object[] params) {
        System.out.println("Event_B onEvent :" + event + " \t" + Hasor.logString(params));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {}
    }
};