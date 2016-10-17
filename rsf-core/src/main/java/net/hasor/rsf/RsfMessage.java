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
package net.hasor.rsf;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 宣布此接口为消息接口,RSF在处理该接口的 RPC 调用请求时会忽略远程机器的 response 。
 * 消息接口的工作方模式下,当确认 rpc 数据包投递到远程机器并进入调用队列,之后方法会立即返回成功。
 *
 * <p>提示: 您可以配合 RSF 的异步调用,使消息发布变为异步消息。</p>
 * @version : 2014年11月12日
 * @author 赵永春(zyc@hasor.net)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RsfMessage {
}