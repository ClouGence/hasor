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
package net.hasor.dataway.spi;
import net.hasor.dataway.DatawayApi;
import net.hasor.dataway.authorization.UiAuthorization;

import java.util.EventListener;

/**
 * 权限判断
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-06-02
 */
public interface AuthorizationChainSpi extends EventListener {
    /**
     * UI 界面上的操作在进入执行之前会先走这个 SPI 进行一次权限校验。
     * @param checkType 界面的操作
     * @param apiId apiId
     * @param defaultCheck 默认通过还是拒绝，如果是 ChainSpi 则是上一个 ChainSpi 的结果。
     * @return 返回 true 或 false 表示通过还是失败。
     */
    public boolean doCheck(UiAuthorization checkType, DatawayApi apiId, boolean defaultCheck);
}