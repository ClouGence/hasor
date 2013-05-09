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
package org.platform.context;
import java.io.File;
import java.io.IOException;
import org.platform.binder.BeanInfo;
import com.google.inject.Injector;
/**
 * 
 * @version : 2013-3-26
 * @author 赵永春 (zyc@byshell.org)
 */
public interface AppContext {
    /**获取系统启动时间*/
    public long getAppStartTime();
    /**获取应用程序配置。*/
    public Settings getSettings();
    /**获取初始化上下文*/
    public InitContext getInitContext();
    /**通过类型创建该类实例，使用guice*/
    public <T> T getInstance(Class<T> beanType);
    //    /**通过名称创建bean实例，使用guice。*/
    //    public  <T extends IService> T getService(String servicesName);
    //    /**通过类型创建该类实例，使用guice*/
    //    public  <T extends IService> T getService(Class<T> servicesType);
    /**获得Guice环境。*/
    public Injector getGuice();
    /**通过名获取Bean的类型。*/
    public <T> Class<T> getBeanType(String name);
    /**获取已经注册的Bean名称。*/
    public String[] getBeanNames();
    /**获取bean信息。*/
    public BeanInfo getBeanInfo(String name);
    /**通过名称创建bean实例，使用guice。*/
    public <T> T getBean(String name);
    /**获取程序工作目录（绝对路径）。*/
    public String getWorkDir();
    /**获取数据文件目录（绝对路径）。*/
    public String getDataDir();
    /**获取临时数据文件目录。*/
    public String getTempDir();
    /**获取缓存目录。*/
    public String getCacheDir();
    /**获取数据文件目录，自动将name属性添加到返回值中。*/
    public String getDataDir(String name);
    /**获取临时数据文件目录，自动将name属性添加到返回值中。*/
    public String getTempDir(String name);
    /**获取缓存目录，自动将name属性添加到返回值中。*/
    public String getCacheDir(String name);
    /**在临时目录下创建一个不重名的临时文件返回，该临时文件会在虚拟机正常退出之后连同其所在目录一同删除。*/
    public File createTempFile() throws IOException;
    /*----------------------------------------------------------------------*/
    /**生成一个UUID字符串，32个字符串长度。*/
    public String genIDBy32();
    /**生成一个UUID字符串，36个字符串长度。*/
    public String genIDBy36();
    /**从时钟服务中获取一个在分布式部署环境里有效的时间。*/
    public long getSyncTime();
    /**获取本地时钟*/
    public long getLocalTime();
    /**
     * 生成路径算法。
     * @param number 数字
     * @param size 每个目录下可以拥有的子目录或文件数目。
     */
    public String genPath(long number, int size);
}