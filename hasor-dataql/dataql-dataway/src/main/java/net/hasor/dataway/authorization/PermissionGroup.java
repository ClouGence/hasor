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
package net.hasor.dataway.authorization;
import net.hasor.utils.StringUtils;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static net.hasor.dataway.authorization.PermissionType.*;

/**
 * 权限分组。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
public enum PermissionGroup {
    /** 分组:全部 */
    Group_Full(new PermissionType[] { ApiInfo, ApiList, ApiHistory, ApiEdit, ApiPublish, ApiDisable, ApiDelete, ApiExecute }),
    /** 分组:只读 */
    Group_ReadOnly(new PermissionType[] { ApiList, ApiInfo, ApiHistory }),
    /** 分组:仅执行 */
    Group_Execute(new PermissionType[] { ApiExecute }),
    ;
    private final PermissionType[] permissionTypes;

    PermissionGroup(PermissionType[] permissionTypes) {
        this.permissionTypes = (permissionTypes == null ? new PermissionType[0] : permissionTypes);
    }

    /** 权限 Code 集 */
    public Set<String> toCodeSet() {
        return Arrays.stream(this.permissionTypes).map(PermissionType::getPermissionCode).collect(Collectors.toSet());
    }

    /** 执行检测权限 */
    public boolean testPermission(PermissionType permissionType) {
        for (PermissionType type : this.permissionTypes) {
            if (type == permissionType) {
                return true;
            }
        }
        return false;
    }

    /** 执行检测权限 */
    public boolean testPermission(String permissionCode) {
        for (PermissionType type : this.permissionTypes) {
            if (StringUtils.equals(type.getPermissionCode(), permissionCode)) {
                return true;
            }
        }
        return false;
    }
}
