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
/**
 * 表示一个工作空间设置
 * @version : 2013-5-23
 * @author 赵永春 (zyc@byshell.org)
 */
public interface WorkSpace {
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
    /**
    * 生成路径算法。
    * @param target 目标
    * @param dirSize 每个目录下可以拥有的子目录或文件数目。
    */
    public String genPath(long number, int size);
    /**获取设置*/
    public Settings getSettings();
}