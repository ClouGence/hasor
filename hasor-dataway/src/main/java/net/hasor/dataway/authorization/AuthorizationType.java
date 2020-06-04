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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 界面操作权限点。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-03
 */
public enum AuthorizationType {
    /** UI - 单个API信息查阅 */
    ApiInfo("api_info", new AuthorizationType[] {}),
    /** UI - 查看API列表 */
    ApiList("api_list", new AuthorizationType[] {}),
    /** UI- 查看API的发布历史列表 */
    ApiHistory("api_history", new AuthorizationType[] {}),
    /** UI- API的编辑和保存操作 */
    ApiEdit("api_edit", new AuthorizationType[] {}),
    /** UI- 接口发布能力(仅:冒烟/发布) */
    ApiPublish("api_publish", new AuthorizationType[] {}),
    /** UI- 已发布接口进行禁用 or 下线 */
    ApiDisable("api_disable", new AuthorizationType[] {}),
    /** UI- 删除一个接口 */
    ApiDelete("api_delete", new AuthorizationType[] {}),
    /** 接口发布之后的调用 */
    ApiExecute("api_execute", new AuthorizationType[] {}),
    //
    //
    /** 分组:全部 */
    Group_Full("group_full", new AuthorizationType[] { ApiInfo, ApiList, ApiHistory, ApiEdit, ApiPublish, ApiDisable, ApiDelete, ApiExecute }),
    /** 分组:只读 */
    Group_ReadOnly("group_readonly", new AuthorizationType[] { ApiList, ApiInfo, ApiHistory }),
    /** 分组:仅执行 */
    Group_Execute("group_execute", new AuthorizationType[] { ApiExecute }),
    ;
    private final String              authorizationCode;
    private final AuthorizationType[] sameAre;

    AuthorizationType(String authorizationCode, AuthorizationType[] sameAre) {
        this.authorizationCode = authorizationCode;
        this.sameAre = (sameAre == null ? new AuthorizationType[0] : sameAre);
    }

    /** 执行检测权限 */
    public Set<String> toCodeSet() {
        Set<String> hasCodes = new HashSet<>();
        hasCodes.add(this.authorizationCode);
        hasCodes.addAll(Arrays.stream(this.sameAre).map(authorizationType -> {
            return authorizationType.authorizationCode;
        }).collect(Collectors.toList()));
        return hasCodes;
    }

    /** 执行检测权限 */
    public boolean testAuthorization(Set<String> codeSet) {
        Set<String> hasCodes = toCodeSet();
        hasCodes.retainAll(codeSet);
        return !hasCodes.isEmpty();
    }
}