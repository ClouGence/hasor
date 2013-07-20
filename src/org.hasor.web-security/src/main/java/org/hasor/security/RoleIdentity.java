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
package org.hasor.security;
/**
 * 表示用户登陆进系统的身份
 * @version : 2013-5-3
 * @author 赵永春 (zyc@byshell.org)
 */
public interface RoleIdentity {
    /**传入一个身份标志测试用户是否匹配该身份条件。*/
    public boolean equals(RoleIdentity identity);
}