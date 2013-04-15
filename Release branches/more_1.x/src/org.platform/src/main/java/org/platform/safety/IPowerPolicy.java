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
package org.platform.safety;
import org.platform.event.InitEvent;
/**
 * 权限判断，可以通过该接口来实现各种不用的权限模型。
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IPowerPolicy {
    /**初始化策略对象。*/
    public void initPolicy(InitEvent event);
    /**
     * 进行策略检查。
     * @param userInfo 用户信息对象。
     * @param powerCode 要检查的权限点。
     */
    public boolean testPermission(IAuthSession authSession, Permission[] powerCode);
}