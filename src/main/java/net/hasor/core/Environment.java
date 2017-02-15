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
package net.hasor.core;
import java.util.Set;
/**
 * 环境支持
 *
 * 环境变量来源以及加载顺序，靠后顺位会覆盖前一顺位的重复配置。
 * <li>1st，配置文件"hasor.environmentVar"</li>
 * <li>2st，"Hasor.put*"的配置</li>
 * <li>3st，属性文件"env.config"</li>
 * <li>4st，System.getProperties()</li>
 * <li>5st，System.getenv()</li>
 *
 * 属性文件可以存在下面两个位置：1.%WORK_HOME% 下、2.classpath路径。 WORK_HOME优先classpath。
 * @version : 2013-6-19
 * @author 赵永春 (zyc@hasor.net)
 */
public interface Environment {
    public static final String EVN_FILE_NAME = "env.config";

    /** @return 获取扫描路径*/
    public String[] getSpanPackage();

    /** @return 获取配置的工作模式。*/
    public String getWorkMode();

    /**在框架扫描包的范围内查找具有特征类集合。（特征可以是继承的类、标记的注解）*/
    public Set<Class<?>> findClass(Class<?> featureType);

    /**
     * 在框架扫描包的范围内查找具有特征类集合（特征可以是继承的类、标记的注解）。<br>
     * -- 该方法会放弃在匹配的过程中如果类无法被ClassLoader所加载的类。
     *
     * @param featureType 特征类型
     * @param loadPackages 扫面范围，单个包
     * @return 返回匹配的类集合。
     */
    public Set<Class<?>> findClass(Class<?> featureType, String loadPackages);

    /**
     * 在框架扫描包的范围内查找具有特征类集合（特征可以是继承的类、标记的注解）。<br>
     * -- 该方法会放弃在匹配的过程中如果类无法被ClassLoader所加载的类。
     *
     * @param featureType 特征类型
     * @param loadPackages 扫面范围，多个包
     * @return 返回匹配的类集合。
     */
    public Set<Class<?>> findClass(Class<?> featureType, String[] loadPackages);

    /** @return 获取上下文*/
    public Object getContext();

    /**获取当创建Bean时使用的{@link ClassLoader}*/
    public ClassLoader getClassLoader();

    /** @return 事件上下文*/
    public EventContext getEventContext();
    //

    /**插件目录*/
    public String getPluginDir(Class<?> pluginType);

    /**工作目录*/
    public String getWorkSpaceDir();
    //
    /*-----------------------------------------------------------------------------------Settings*/

    /** @return 获取应用程序配置。*/
    public Settings getSettings();
    //
    /*----------------------------------------------------------------------------------------Env*/
    /**获取工作模式，工作路径的配置可以在config.xml的“<b>environmentVar.WORK_MODE</b>”节点上配置。*/
    public static final String WORK_MODE             = "WORK_MODE";
    /**获取工作目录，工作路径的配置可以在config.xml的“<b>environmentVar.WORK_HOME</b>”节点上配置。*/
    public static final String WORK_HOME             = "WORK_HOME";
    /**获取临时文件存放目录，工作路径的配置可以在config.xml的“<b>environmentVar.HASOR_TEMP_PATH</b>”节点上配置。*/
    public static final String HASOR_TEMP_PATH       = "HASOR_TEMP_PATH";
    /**获取工作空间中专门用于存放日志的目录空间，配置可以在config.xml的“<b>environmentVar.HASOR_LOG_PATH</b>”节点上配置。*/
    public static final String HASOR_LOG_PATH        = "HASOR_LOG_PATH";
    /**获取工作空间中专门用于存放模块配置信息的目录空间，配置可以在config.xml的“<b>environmentVar.HASOR_PLUGIN_PATH</b>”节点上配置。*/
    public static final String HASOR_PLUGIN_PATH     = "HASOR_PLUGIN_PATH";
    /**获取工作空间中专门用于存放模块配置信息的目录空间，配置可以在config.xml的“<b>environmentVar.HASOR_PLUGIN_SETTINGS</b>”节点上配置。*/
    public static final String HASOR_PLUGIN_SETTINGS = "HASOR_PLUGIN_SETTINGS";
    //

    /**
     * 计算字符串，将字符串中定义的环境变量替换为环境变量值。环境变量名不区分大小写。<br>
     * <font color="ff0000"><b>注意</b></font>：只有被百分号包裹起来的部分才被解析成为环境变量名，
     * 如果无法解析某个环境变量平台会抛出一条警告，并且将环境变量名连同百分号作为环境变量值一起返回。<br>
     * <font color="00aa00"><b>例如</b></font>：环境中定义了变量Hasor_Home=C:/hasor、Java_Home=c:/app/java，下面的解析结果为
     * <div>%hasor_home%/tempDir/uploadfile.tmp&nbsp;&nbsp;--&gt;&nbsp;&nbsp;C:/hasor/tempDir/uploadfile.tmp</div>
     * <div>%JAVA_HOME%/bin/javac.exe&nbsp;&nbsp;--&gt;&nbsp;&nbsp;c:/app/java/bin/javac.exe</div>
     * <div>%work_home%/data/range.png&nbsp;&nbsp;--&gt;&nbsp;&nbsp;%work_home%/data/range.png；并伴随一条警告</div>
     * @param eval 环境变量表达式。
     * @return 返回环境变量表达式计算结果。
     */
    public String evalString(String eval);

    /**
     * 添加环境变量，添加的环境变量并不会影响到系统环境变量，它会使用内部Map保存环境变量从而避免影响JVM正常运行。
     * @param varName 环境变量名。
     * @param value 环境变量值或环境变量表达式。
     */
    public void addEnvVar(String varName, String value);

    /**
     * 删除环境变量，该方法从内部Map删除所保存的环境变量，这样做的目的是为了避免影响JVM正常运行。
     * @param varName 环境变量名。
     */
    public void removeEnvVar(String varName);

    /**刷新加载的环境变量*/
    public void refreshVariables();

    /**获取系统属性。*/
    public String getSystemProperty(String property);
}