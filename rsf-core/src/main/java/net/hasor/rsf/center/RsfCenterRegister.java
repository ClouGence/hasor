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
package net.hasor.rsf.center;
import net.hasor.rsf.RsfService;
import net.hasor.rsf.center.domain.PublishInfo;
/**
 * 服务发布接口，该接口需要远端注册中心实现
 * @version : 2016年2月18日
 * @author 赵永春(zyc@hasor.net)
 */
@RsfService(group = "RSF", version = "1.0.0")
public interface RsfCenterRegister {
    /**发布服务
     * @return 返回订阅ID，当服务下线时需要使用这个ID进行解除发布。*/
    public String publishService(String hostString, PublishInfo info);
    /** 订阅服务
     * @return 返回订阅ID，当服务下线时需要使用这个ID进行解除订阅。*/
    public String receiveService(String hostString, PublishInfo info);
    /**根据订阅ID删除订阅信息。*/
    public boolean removeRegister(String hostString, String registerID);
}