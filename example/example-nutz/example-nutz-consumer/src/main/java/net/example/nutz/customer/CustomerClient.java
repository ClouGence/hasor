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
package net.example.nutz.customer;
import net.example.domain.consumer.EchoService;
import net.example.domain.consumer.MessageService;
import net.hasor.core.AppContext;
import net.hasor.rsf.RsfResult;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
/**
 *
 * @version : 2014年9月12日
 * @author 赵永春(zyc@hasor.net)
 */
public class CustomerClient {
    public static void main(String[] args) throws Throwable {
        Ioc ioc = new NutIoc(new ComboIocLoader("*js", "ioc/", "*hasor"));
        //
        // .启动 Hasor
        ioc.get(AppContext.class);
        System.out.println("server start.");
        //
        //Client -> Server
        EchoService echoService = ioc.get(EchoService.class);
        for (int i = 0; i < 200; i++) {
            Thread.sleep(100);
            try {
                String res = echoService.sayHello("Hello Word");
                System.out.println(res);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        //
        MessageService messageService = ioc.get(MessageService.class);
        for (int i = 0; i < 200; i++) {
            try {
                RsfResult res = messageService.sayHello("Hello Word");//客户端会瞬间返回,服务端执行一个消息需要 500毫秒。
                System.out.println(res);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        //
        //        UserService userService = ioc.get(UserService.class);
        //        List<UserDO> userDOS = userService.queryUser();
        //        System.out.println(JSON.toString(userDOS));
        //
        ioc.depose();
    }
}