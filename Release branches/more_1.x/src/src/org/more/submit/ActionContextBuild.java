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
package org.more.submit;
import java.io.File;
import org.more.util.Config;
/**
 * ActionContextBuild是提供给外壳支撑环境生成器的接口，
 * 任何more的外壳支撑环境通过SubmitBuild生成时都需要实现该接口。
 * @version 2010-7-26
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionContextBuild {
    /**初始化这个生成器，参数configFile，String类型对象表示配置文件位置。
     * @throws Throwable */
    public void init(Config config) throws Throwable;
    /**调用生成器生成ActionContext对象，ActionContext会经过SubmitBuild再次生成为SubmitContext*/
    public ActionContext getActionContext();
    /**设置基本目录。如果使用相对路径则是相对这个路径的。*/
    public void setBaseDir(File baseDir);
};