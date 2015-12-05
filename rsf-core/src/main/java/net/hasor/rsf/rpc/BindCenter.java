/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package net.hasor.rsf.rpc;
import java.util.List;
import net.hasor.core.Provider;
import net.hasor.rsf.RsfBindInfo;
import net.hasor.rsf.RsfBinder;
/**
 * 注册中心
 * @version : 2014年11月30日
 * @author 赵永春(zyc@hasor.net)
 */
public interface BindCenter {
    /**获取RsfBinder*/
    public RsfBinder getRsfBinder();
 
    //
    /**回收已经发布的服务*/
    public void recoverService(RsfBindInfo<?> bindInfo);
}