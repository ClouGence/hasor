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
package net.hasor.rsf.center.server.task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import net.hasor.core.Inject;
import net.hasor.rsf.center.server.manager.InitServerManager;
import net.hasor.rsf.center.server.utils.DateCenterUtils;
/**
 *
 * @version : 2016年5月17日
 * @author 赵永春(zyc@hasor.net)
 */
public class ServerBeatTask implements TimerTask {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Inject
    private InitServerManager initServerManager;
    @Override
    public void run(Timeout timeout) throws Exception {
        //
        try {
            this.logger.info("task[ServerBeatTask] -> running at :" + DateCenterUtils.timestamp());
            this.initServerManager.beatServer();
        } catch (Exception e) {
            this.logger.error("task[ServerBeatTask] -> error : " + e.getMessage(), e);
        }
        //
        //        // -Beat(心跳)
        //        this.timerManager = new TimerManager(15000, "RsfCenter-Beat");
        //        this.timerManager.atTime(this);
    }
}