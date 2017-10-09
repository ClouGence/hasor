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
package net.hasor.rsf.utils;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import net.hasor.core.Hasor;
import net.hasor.utils.NameThreadFactory;

import java.util.concurrent.TimeUnit;
/**
 *
 * @version : 2015年3月28日
 * @author 赵永春(zyc@hasor.net)
 */
public class TimerManager {
    private final int   defaultTimeout;
    private final Timer timer;
    //
    public TimerManager(int defaultTimeout, ClassLoader loader) {
        this(defaultTimeout, "RSF", loader);
    }
    public TimerManager(int defaultTimeout, String name, ClassLoader loader) {
        this.defaultTimeout = defaultTimeout;
        name = Hasor.assertIsNotNull(name);
        this.timer = new HashedWheelTimer(new NameThreadFactory(name + "-Timer-%s", loader));
    }
    public void atTime(TimerTask timeTask) {
        this.atTime(timeTask, this.defaultTimeout);
    }
    public void atTime(TimerTask timeTask, int timeout) {
        int reqTimeout = validateTimeout(timeout);
        this.timer.newTimeout(timeTask, reqTimeout, TimeUnit.MILLISECONDS);
    }
    private int validateTimeout(int timeout) {
        if (timeout <= 0) {
            timeout = defaultTimeout;
        }
        return timeout;
    }
}