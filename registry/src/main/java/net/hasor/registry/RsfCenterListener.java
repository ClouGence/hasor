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
package net.hasor.registry;
import net.hasor.registry.domain.client.CenterEventBody;
import net.hasor.rsf.RsfService;
/**
 * 接收来自注册中心的消息。
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
@RsfService(group = "RSF", version = "1.0.0")
public interface RsfCenterListener {
    /**
     * 接收来自注册中心的消息
     * @param eventType 事件类型
     * @param centerEventBody 内容
     * @return 返回事件处理是否成功
     * @throws Throwable 如果事件处理失败则引发的错误。
     */
    public boolean onEvent(String eventType, CenterEventBody centerEventBody) throws Throwable;
}