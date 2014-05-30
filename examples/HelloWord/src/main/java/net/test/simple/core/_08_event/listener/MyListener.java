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
package net.test.simple.core._08_event.listener;
import net.hasor.core.EventListener;
import net.hasor.core.Hasor;
/**
 * 收到事件，同时线程沉睡500毫秒延迟。
 * @version : 2014-1-11
 * @author 赵永春 (zyc@byshell.org)
 */
public class MyListener implements EventListener {
    public void onEvent(String event, Object[] params) throws InterruptedException {
        Thread.sleep(500);
        System.out.println("Receive Message:" + Hasor.logString(params));
    }
};