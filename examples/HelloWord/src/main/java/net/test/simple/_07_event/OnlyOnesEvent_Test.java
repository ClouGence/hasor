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
package net.test.simple._07_event;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.EventListener;
import net.hasor.quick.anno.AnnoStandardAppContext;
import org.junit.Test;
/**
 * 仅会被执行一次的事件
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class OnlyOnesEvent_Test {
    private static String config = "net/test/simple/_05_event/event-config.xml";
    //
    /*测试，异步事件*/
    @Test
    public void test_OnlyOnesEvent() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>test_OnlyOnesEvent<<--");
        AnnoStandardAppContext appContext = new AnnoStandardAppContext(config);
        appContext.start();
        //获取事件管理器
        //push 一个事件监听器，该监听器在 push 之后只会生效一次。
        appContext.pushListener("MyEvent", new MyEventListener());
        //引发4次事件，但是只有一个事件会被处理。
        appContext.fireSyncEvent("MyEvent");
        appContext.fireSyncEvent("MyEvent");
        appContext.fireSyncEvent("MyEvent");
        appContext.fireSyncEvent("MyEvent");
        //
        //再次注册 MyEvent 事件
        appContext.pushListener("MyEvent", new MyEventListener());
        //引发事件
        appContext.fireSyncEvent("MyEvent");
    }
    private static class MyEventListener implements EventListener {
        public void onEvent(String event, Object[] params) throws Throwable {
            System.out.println("onEvent");
        }
    }
}
