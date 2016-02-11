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
package net.test.hasor.core._08_event.listener;
import net.hasor.core.EventListener;
import net.test.hasor.core._08_event.custom.Listener;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
/**
 * 收到事件，同时线程沉睡500毫秒延迟。
 * @version : 2014-1-11
 * @author 赵永春 (zyc@byshell.org)
 */
@Listener("TestEvent")
public class MyListener2 implements EventListener {
    public void onEvent(String event, Object[] params) throws InterruptedException {
        Thread.sleep(500);
        System.out.println("Receive Message:" + ReflectionToStringBuilder.toString(params, ToStringStyle.SIMPLE_STYLE));
    }
};