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
package net.hasor.rsf.center.server.push;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.hasor.core.Inject;
import net.hasor.rsf.RsfClient;
import net.hasor.rsf.RsfContext;
import net.hasor.rsf.address.InterAddress;
/**
 * 执行处理器
 * @version : 2016年3月23日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class PushProcessor {
    protected Logger   logger = LoggerFactory.getLogger(getClass());
    @Inject
    private RsfContext rsfContext;
    //
    /**获取RsfContext。*/
    protected final RsfContext getRsfContext() {
        return this.rsfContext;
    }
    //
    public final void doProcessor(PushEvent event) {
        if (event == null) {
            return;
        }
        if (event.getTarget() == null || event.getTarget().isEmpty()) {
            RsfClient rsfClient = this.getRsfContext().getRsfClient();
            doCall(event, rsfClient);
        } else {
            for (String target : event.getTarget()) {
                try {
                    InterAddress rsfAddress = new InterAddress(target);
                    RsfClient rsfClient = this.getRsfContext().getRsfClient(rsfAddress);
                    doCall(event, rsfClient);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
    private void doCall(PushEvent event, RsfClient rsfClient) {
        try {
            if (rsfClient != null) {
                this.doProcessor(rsfClient, event);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }
    public abstract void doProcessor(RsfClient rsfClient, PushEvent event) throws Throwable;
}