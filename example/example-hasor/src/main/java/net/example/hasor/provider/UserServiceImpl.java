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
package net.example.hasor.provider;
import net.example.domain.consumer.UserService;
import net.example.domain.domain.UserDO;
import net.example.hasor.services.UserManager;
import net.hasor.core.Inject;
import net.hasor.utils.ExceptionUtils;

import java.util.List;
/**
 * 服务实现
 * @version : 2016年11月07日
 * @author 赵永春(zyc@hasor.net)
 */
public class UserServiceImpl implements UserService {
    @Inject
    private UserManager userManager;
    @Override
    public List<UserDO> queryUser() {
        try {
            List<UserDO> userDOs = userManager.queryList();
            return userDOs;
        } catch (Exception e) {
            throw ExceptionUtils.toRuntimeException(e);
        }
    }
}