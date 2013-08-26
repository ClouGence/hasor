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
package org.test.event;

import org.hasor.context.HasorEventListener;
import org.hasor.context.anno.EventListener;
/**
 * 
 * @version : 2013-8-21
 * @author ’‘”¿¥∫(zyc@hasor.net)
 */
@EventListener("HelloEvent")
public class CustomEvent implements HasorEventListener{
    public void onEvent(String event, Object[] params) throws Throwable {
        System.out.println(params[0]);
    }
}