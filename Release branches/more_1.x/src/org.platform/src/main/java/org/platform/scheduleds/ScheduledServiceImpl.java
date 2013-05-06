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
package org.platform.scheduleds;
import static org.platform.PlatformConfig.Platform_TaskPool_MaxSize;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
/**
 * 
 * @version : 2013-5-6
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
class ScheduledServiceImpl implements ScheduledService {
    private ScheduledExecutorService scheduledService = null;
    public void executeTask() {}
    @Override
    public Future executeTask(Runnable runnable) {
        if (this.scheduledService == null)
            this.scheduledService = Executors.newScheduledThreadPool(this.getSettings().getInteger(Platform_TaskPool_MaxSize, 5));
        
       return this.scheduledService.submit(runnable)
        
    }
}