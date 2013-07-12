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
package org.hasor.context;
import java.util.Map;
/**
 * 环境变量操作
 * @version : 2013-6-19
 * @author 赵永春 (zyc@byshell.org)
 */
public interface Environment {
    /**获取工作空间接口*/
    public WorkSpace getWorkSpace();
    /**计算字符串，将字符串中定义的环境变量替换为环境变量值。环境变量名不区分大小写。<br/>
     * <font color="ff0000"><b>注意</b></font>：只有被百分号包裹起来的部分才被解析成为环境变量名，
     * 如果无法解析某个环境变量平台会抛出一条警告，并且将环境变量名连同百分号作为环境变量值一起返回。<br/>
     * <font color="00aa00"><b>例如</b></font>：环境中定义了变量Hasor_Home=C:/hasor、Java_Home=c:/app/java，下面的解析结果为
     * <div>%hasor_home%/tempDir/uploadfile.tmp&nbsp;&nbsp;--&gt;&nbsp;&nbsp;C:/hasor/tempDir/uploadfile.tmp</div>
     * <div>%JAVA_HOME%/bin/javac.exe&nbsp;&nbsp;--&gt;&nbsp;&nbsp;c:/app/java/bin/javac.exe</div>
     * <div>%work_home%/data/range.png&nbsp;&nbsp;--&gt;&nbsp;&nbsp;%work_home%/data/range.png；并伴随一条警告</div>
     * */
    public String evalString(String eval);
    /**根据环境变量名称获取环境变量的值，如果不存在该环境变量的定义则返回null.*/
    public String getEnvVar(String varName);
    /**添加环境变量，添加的环境变量并不会影响到系统环境变量，它会使用内部Map保存环境变量从而避免影响JVM正常运行。*/
    public void addEnvVar(String varName, String value);
    /**删除环境变量，该方法从内部Map删除所保存的环境变量，这样做的目的是为了避免影响JVM正常运行。*/
    public void remoteEnvVar(String varName);
    /**获取定义的环境变量集合，Map形式返回。*/
    public Map<String, String> getEnv();
}