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
package net.test.simple._05_event;
import java.io.IOException;
import java.net.URISyntaxException;
import net.hasor.core.context.AnnoStandardAppContext;
import org.junit.Test;
/**
 * 异步步事件演示
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class AsyncEvent_Test {
    private static String config = "net/test/simple/_05_event/event-config.xml";
    public static String  Type_A = "Event_A";
    public static String  Type_B = "Event_B";
    //
    /*测试，异步事件*/
    @Test
    public void test_AsyncEvent() throws IOException, URISyntaxException, InterruptedException {
        System.out.println("--->>test_AsyncEvent<<--");
        AnnoStandardAppContext appContext = new AnnoStandardAppContext(config);
        appContext.start();
        //
        for (int i = 0; i < 10; i++)
            /*发送异步事件*/
            appContext.getEventManager().doAsync(Type_B, i);
        System.out.println("after Event do sth...");
        /*由于event-config.xml中配置了 3 个事件线程池，因此超过 3 个以上的事件将会排队处理。*/
        System.in.read();
    }
}