/*
 * Copyright 2008-2009 the original ÕÔÓÀ´º(zyc@hasor.net).
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
package net.test.simple._04_settings;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import net.hasor.core.Hasor;
import net.hasor.core.Settings;
import net.hasor.core.context.AnnoStandardAppContext;
import org.junit.Test;
/**
 * 
 * @version : 2013-7-16
 * @author ÕÔÓÀ´º (zyc@hasor.net)
 */
public class SimpleSettingsTest {
    @Test
    public void testSimple() throws IOException, URISyntaxException {
        System.out.println("--->>testSimple<<--");
        AnnoStandardAppContext appContext = new AnnoStandardAppContext("net/test/simple/_04_settings/simple-config.xml");
        appContext.start();
        Settings settings = appContext.getSettings();
        //
        //
        //
        String myName = settings.getString("mySelf.myName");
        Hasor.logInfo("my Name is %s.", myName);
        //
        int myAge = settings.getInteger("mySelf.myAge");
        Hasor.logInfo("my Age is %s.", myAge);
        //
        Date myBirthday = settings.getDate("mySelf.myBirthday");
        Hasor.logInfo("my Birthday is %s.", myBirthday);
        //
        String myWork = settings.getString("mySelf.myWork");
        Hasor.logInfo("my Work is %s.", myWork);
        //
        String myProjectURL = settings.getString("mySelf.myProjectURL");
        Hasor.logInfo("my Project is %s.", myProjectURL);
    }
}