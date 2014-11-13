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
package net.hasor.rsf._test._;
import java.util.concurrent.atomic.AtomicBoolean;
import net.hasor.rsf.metadata.ServiceMetaData;
/**
 * 服务发布者
 * @version : 2014年9月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServiceProvider {
    private ServiceMetaData metaData = new ServiceMetaData();
    private AtomicBoolean   inited   = new AtomicBoolean(false);
    /**初始化服务*/
    public void initService() {
        if (!this.inited.compareAndSet(false, true)) {
            return;/*避免被初始化多次*/
        }
        //
    }
}