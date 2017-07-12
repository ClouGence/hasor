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
package net.example.hasor.provider;
import net.example.domain.consumer.EchoService;
import net.hasor.core.InjectSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 在你call rpc 时候，远程服务器会回应你一句 它在配置文件里配置的信息，配置的信息是通过依赖注入获取的。
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
public class EchoServiceImpl implements EchoService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @InjectSettings("myApp.configString")
    private String messageToYou;
    //
    @Override
    public String sayHello(String echo) {
        String youSay = "you say " + echo;
        logger.info(youSay);
        return youSay + " , server to You -> " + this.messageToYou;
    }
}