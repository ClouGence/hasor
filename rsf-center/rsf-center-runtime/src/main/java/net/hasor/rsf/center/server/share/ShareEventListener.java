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
package net.hasor.rsf.center.server.share;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.center.server.push.PushEvent;
/**
 * 注册中心之间分享事件的远程接口。
 * @version : 2016年4月11日
 * @author 赵永春(zyc@hasor.net)
 */
@RsfService(group = "RSF", version = "1.0.0")
public interface ShareEventListener {
    /**分享事件*/
    public boolean receiveShareEvent(PushEvent eventData);
}