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
package org.platform.api.safety;
import java.util.Map;
/**
 * 负责登陆认证
 * @version : 2013-3-12
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IAuthorization {
    /**登陆系统*/
    public IUser login(Map<String, Object> params);
    /**退出系统*/
    public void exit(IUser userInfo);
}