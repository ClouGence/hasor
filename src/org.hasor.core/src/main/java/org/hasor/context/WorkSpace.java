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
import java.io.File;
import java.io.IOException;
/**
 * 应用程序工作空间设置
 * @version : 2013-5-23
 * @author 赵永春 (zyc@byshell.org)
 */
public interface WorkSpace {
    /**获取{@link Settings}接口方法。*/
    public Settings getSettings();
    /**获取工作目录，工作路径的配置可以在config.xml的“<b>workspace.workDir</b>”节点上配置。<br/>
     * <font color="00aa00"><b>提示</b></font>：该节点的配置内容支持环境变量解析。*/
    public String getWorkDir();
    /**获取数据文件存放目录，工作路径的配置可以在config.xml的“<b>workspace.dataDir</b>”节点上配置。<br/>
     * <font color="00aa00"><b>提示</b></font>：该节点的配置内容支持环境变量解析。*/
    public String getDataDir();
    /**获取临时文件存放目录，工作路径的配置可以在config.xml的“<b>workspace.tempDir</b>”节点上配置。<br/>
     * <font color="00aa00"><b>提示</b></font>：该节点的配置内容支持环境变量解析。*/
    public String getTempDir();
    /**获取缓存文件存放目录，工作路径的配置可以在config.xml的“<b>workspace.cacheDir</b>”节点上配置。<br/>
     * <font color="00aa00"><b>提示</b></font>：该节点的配置内容支持环境变量解析。*/
    public String getCacheDir();
    /**获取工作空间中专门用于存放模块配置信息的目录空间，配置可以在config.xml的“<b>workspace.pluginDir</b>”节点上配置。<br/>
     * <font color="00aa00"><b>提示</b></font>：该节点的配置内容支持环境变量解析。*/
    public String getPluginDir();
    /**获取工作空间中专门用于存放日志的目录空间，配置可以在config.xml的“<b>workspace.logDir</b>”节点上配置。<br/>
     * <font color="00aa00"><b>提示</b></font>：该节点的配置内容支持环境变量解析。*/
    public String getLogDir();
    /**基于{@link #getDataDir()}的路径作为父路径，返回由subPath参数所表示的数据目录。<br/>
     * <font color="00aa00"><b>提示</b></font>：参数中支持包含环境变量。*/
    public String getDataDir(String subPath);
    /**基于{@link #getTempDir()}的路径作为父路径，返回由subPath参数所表示的临时目录。<br/>
     * <font color="00aa00"><b>提示</b></font>：参数中支持包含环境变量。*/
    public String getTempDir(String subPath);
    /**基于{@link #getCacheDir()}的路径作为父路径，返回由subPath参数所表示的缓存目录。<br/>
     * <font color="00aa00"><b>提示</b></font>：参数中支持包含环境变量。*/
    public String getCacheDir(String subPath);
    /**基于{@link #getPluginDir()}的路径作为父路径，返回由model参数所表示的模块私有空间。<br/>
     * <font color="00aa00"><b>提示</b></font>：参数中支持包含环境变量。*/
    public String getPluginDir(Class<?> hasorModule);
    /**基于{@link #getLogDir()}的路径作为父路径，返回由subPath参数所表示的日志目录。<br/>
     * <font color="00aa00"><b>提示</b></font>：参数中支持包含环境变量。*/
    public String getLogDir(String subPath);
    /**在临时目录下创建一个不重名的临时文件返回，该临时文件会在虚拟机正常退出之后连同其所在目录一同删除。*/
    public File createTempFile() throws IOException;
    /**
    * 生成路径算法生成一个Path
    * @param target 目标
    * @param dirSize 每个目录下可以拥有的子目录或文件数目。
    */
    public String genPath(long number, int size);
}