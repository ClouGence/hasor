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
package net.test.hasor.core._06_context;
import java.util.Date;
import java.util.Set;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Hasor;
import net.hasor.core.Module;
import net.hasor.core.Settings;
import net.hasor.core.context.TemplateAppContext;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 1.findClassTest
 *      类扫描
 * 2.variablesTest
 *      环境变量的解析
 * 3.settingsTest
 *      配置信息读取
 * 
 * @version : 2013-8-11
 * @author 赵永春 (zyc@hasor.net)
 */
public class ContextTest {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //
    // - 类扫描
    @Test
    public void findClassTest() {
        System.out.println("--->>findClassTest<<--");
        //1.创建一个标准的 Hasor 容器。
        AppContext appContext = Hasor.createAppContext();
        //
        //1.查找所有Hasor模块（实现了Module接口的类）。
        Set<Class<?>> facesFeature = appContext.getEnvironment().findClass(Module.class);
        logger.info("find " + facesFeature);
        //2.查找AbstractAppContext的子类
        Set<Class<?>> subFeature = appContext.getEnvironment().findClass(TemplateAppContext.class);
        logger.info("find " + subFeature);
    }
    //
    // - 环境变量的解析
    @Test
    public void variablesTest() {
        System.out.println("--->>variablesTest<<--");
        System.setProperty("MyVar", "hello");
        AppContext appContext = Hasor.createAppContext();
        Environment env = appContext.getEnvironment();
        //
        //JAVA_HOME
        System.out.println(env.envVar("JAVA_HOME"));
        //WORK_HOME，该环境变量由 hasor 的配置文件提供，仅在Hasor框架内有效
        System.out.println(env.envVar("WORK_HOME"));
        //javac.exe
        System.out.println(env.evalString("%JAVA_HOME%/bin/javac.exe"));
        //系统环境变量属性
        System.out.println(env.envVar("MyVar"));
        System.out.println(env.evalString("i say %MyVar%."));
    }
    //
    // - 配置信息读取
    @Test
    public void settingsTest() {
        System.out.println("--->>settingsTest<<--");
        AppContext appContext = Hasor.createAppContext("simple-config.xml");
        Settings settings = appContext.getEnvironment().getSettings();
        //
        String myName = settings.getString("mySelf.myName");
        logger.info("my Name is {}.", myName);
        //
        Integer myAge = settings.getInteger("mySelf.myAge");
        logger.info("my Age is {}.", myAge);
        //
        Date myBirthday = settings.getDate("mySelf.myBirthday");
        logger.info("my Birthday is {}.", myBirthday);//需要解决通用格式转换问题
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