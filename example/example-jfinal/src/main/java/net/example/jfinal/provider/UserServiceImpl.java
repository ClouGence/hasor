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
package net.example.jfinal.provider;
import net.example.domain.consumer.UserService;
import net.example.domain.domain.UserDO;
import net.example.jfinal.domain.UserDTO;
import net.example.jfinal.services.UserManager;
import net.hasor.core.Inject;
import net.hasor.core.utils.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * 服务实现
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserServiceImpl implements UserService {
    @Inject
    private UserManager userManager;
    //
    //
    @Override
    public List<UserDO> queryUser() {
        // 远程 RPC 接口实现，不建议将底层 JFinal 数据模型透出给客户端，因此这里做数据转换。
        // tips：JFinal底层 Module，不是一个纯粹的 domain ，它是带有 AR 性质的富Domain。透出富Domain，相当于暴露数据结构给客户端，有数据安全风险。
        try {
            List<UserDTO> userDOs = userManager.queryList();
            List<UserDO> userList = new ArrayList<UserDO>();
            for (UserDTO user : userDOs) {
                UserDO userDO = new UserDO();
                userDO.setId(user.getLong("id"));
                userDO.setAccount(user.getStr("account"));
                userDO.setNick(user.getStr("nick"));
                userDO.setEmail(user.getStr("email"));
                userDO.setPassword(user.getStr("password"));
                userDO.setModifyTime(user.getDate("modify_time"));
                userDO.setCreateTime(user.getDate("create_time"));
                userList.add(userDO);
            }
            return userList;
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}