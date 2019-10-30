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
package net.hasor.tconsole.binder;
import net.hasor.core.AppContext;
import net.hasor.tconsole.TelContext;
import net.hasor.tconsole.spi.TelStopContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @version : 2019年10月30日
 * @author 赵永春 (zyc@hasor.net)
 */
class TriggerOnStopToContext implements TelStopContextListener {
    private static Logger     logger = LoggerFactory.getLogger(TriggerOnStopToContext.class);
    private        AppContext appContext;
    private        boolean    enable = true;

    public TriggerOnStopToContext(AppContext appContext) {
        this.appContext = appContext;
    }

    @Override
    public void onStop(TelContext telContext) {
        if (this.enable && this.appContext.isStart()) {
            logger.info("tConsole -> answer quit command, shutdown Hasor.");
            this.appContext.shutdown();
        }
    }

    public void disable() {
        this.enable = false;
    }
}