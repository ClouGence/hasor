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

/**
 * 界面操作权限点。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
public enum PermissionType {
    /** UI - 单个API信息查阅 */
    ApiInfo("api_info"),
    /** UI - 查看API列表 */
    ApiList("api_list"),
    /** UI- 查看API的发布历史列表 */
    ApiHistory("api_history"),
    /** UI- API的编辑和保存操作 */
    ApiEdit("api_edit"),
    /** UI- 接口发布能力(仅:冒烟/发布) */
    ApiPublish("api_publish"),
    /** UI- 已发布接口进行禁用 or 下线 */
    ApiDisable("api_disable"),
    /** UI- 删除一个接口 */
    ApiDelete("api_delete"),
    /** 接口发布之后的调用 */
    ApiExecute("api_execute"),
    ;
    private final String permissionCode;

    PermissionType(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionCode() {
        return this.permissionCode;
    }

    public static PermissionType ofPermissionCode(String permissionCode) {
        for (PermissionType permissionType : PermissionType.values()) {
            if (StringUtils.equalsIgnoreCase(permissionCode, permissionType.permissionCode)) {
                return permissionType;
            }
        }
        return null;
    }
}
