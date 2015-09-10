/*
 * Copyright 2008-2009 the original 赵永春(zyc@LoggerHelper.net).
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
package net.test.hasor.core._10_settings;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import net.hasor.core.AppContext;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 读取配置文件中的内容。
 * @version : 2013-7-16
 * @author 赵永春 (zyc@LoggerHelper.net)
 */
public class SimpleSettingsTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Test
    public void simpleSettingsTest() throws IOException, URISyntaxException {
        System.out.println("--->>simpleSettingsTest<<--");
        AppContext appContext = Hasor.createAppContext("net/test/simple/core/_10_settings/simple-config.xml");
        Settings settings = appContext.getEnvironment().getSettings();
        //
        String myName = settings.getString("mySelf.myName");
        logger.info("my Name is {}.", myName);
        //
        int myAge = settings.getInteger("mySelf.myAge");
        logger.info("my Age is {}.", myAge);
        //
        Date myBirthday = settings.getDate("mySelf.myBirthday");
        logger.info("my Birthday is {}.", myBirthday);//TODO 需要解决通用格式转换问题
        //
        String myWork = settings.getString("mySelf.myWork");
        logger.info("my Work is {}.", myWork);
        //
        String myProjectURL = settings.getString("mySelf.myProjectURL");
        logger.info("my Project is {}.", myProjectURL);
        //
        String[] packages = settings.getStringArray("LoggerHelper.loadPackages");
        logger.info("my packages is {}.", (Object) packages);
    }
}