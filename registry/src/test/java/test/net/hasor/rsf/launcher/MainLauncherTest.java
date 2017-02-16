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
package test.net.hasor.rsf.launcher;
import net.hasor.registry.boot.launcher.MainLauncher;
import net.hasor.rsf.utils.ResourcesUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @version : 2016年3月29日
 * @author 赵永春(zyc@hasor.net)
 */
public class MainLauncherTest {
    protected static Logger logger = LoggerFactory.getLogger(MainLauncherTest.class);
    //
    @Test
    public void doStart() throws Throwable {
        logger.info(">>>>>>>>>>>>>>>>> doStart <<<<<<<<<<<<<<<<<");
        String[] config = new String[2];
        config[0] = "start";
        config[1] = ResourcesUtils.getResource("/center/rsf-server-alone.xml").getFile();
        MainLauncher.doStart(config);
    }
    @Test
    public void doStop() throws Throwable {
        logger.info(">>>>>>>>>>>>>>>>> doStop <<<<<<<<<<<<<<<<<");
        String[] config = new String[2];
        config[0] = "stop";
        config[1] = ResourcesUtils.getResource("/center/rsf-server-alone.xml").getFile();
        MainLauncher.doStop(config);
    }
}