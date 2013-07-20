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
 * 
 * @version : 2013-5-3
 * @author 赵永春 (zyc@byshell.org)
 */
public class RoleIdentityUtil {
    /**根据类型创建一个{@link RoleIdentity}接口对象*/
    public static RoleIdentity getTypeIdentity(Class<?> type) {
        return new ClassTypeIdentity(type);
    }
    private static class ClassTypeIdentity implements RoleIdentity {
        private Class<?> userInfoType = null;
        public ClassTypeIdentity(Class<?> userInfoType) {
            this.userInfoType = userInfoType;
        }
        @Override
        public boolean equals(RoleIdentity identity) {
            if (identity == null)
                return false;
            if (identity instanceof ClassTypeIdentity)
                return this.userInfoType.isAssignableFrom(((ClassTypeIdentity) identity).userInfoType);
            return false;
        }
    }
}