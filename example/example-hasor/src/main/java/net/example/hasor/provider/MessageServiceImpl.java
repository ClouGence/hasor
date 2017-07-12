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
import net.example.domain.consumer.MessageService;
import net.hasor.rsf.RsfResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 无责任消息推送
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
public class MessageServiceImpl implements MessageService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public RsfResult sayHello(String echo) {
        logger.info("you say " + echo);
        return null; //  <-- 标记了 @RsfMessage 的服务接口，其执行结果及可能抛出的异常都会被客户端忽略，因此返回值变得无意义。
    }
}