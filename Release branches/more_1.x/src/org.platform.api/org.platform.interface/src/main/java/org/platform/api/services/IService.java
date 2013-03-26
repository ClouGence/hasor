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
package org.platform.api.services;
import org.platform.api.event.InitEvent;
/**
 * 声明该类为一个服务类，
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IService {
    /**通知服务启动。*/
    public void start(InitEvent event);
    /**通知服务销毁。*/
    public void destroyed();
}