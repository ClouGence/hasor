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
import org.hasor.Assert;
/**
 * 代表一个权限点。
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public class Permission {
    private String perCode = null;
    //
    public Permission(String perCode) {
        Assert.isNotNull(perCode, "permission code is null.");
        this.perCode = perCode;
    }
    /**获取权限点的Code值。*/
    public String getPermissionCode() {
        return this.perCode;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj instanceof Permission) {
            String preCode = ((Permission) obj).getPermissionCode();
            return this.hashCode() == preCode.hashCode();
        } else if (obj instanceof String) {
            return this.perCode.equals(obj);
        }
        return super.equals(obj);
    }
    @Override
    public int hashCode() {
        return this.perCode.hashCode();
    }
}