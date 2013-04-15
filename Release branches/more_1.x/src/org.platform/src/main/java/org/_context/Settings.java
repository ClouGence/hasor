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
package org.platform.runtime._context;
import org.more.core.global.Global;
/**
 * 
 * @version : 2013-1-10
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class Settings {
    public abstract Global getGlobal();
    //-----------------------------------------------------------------
    /**获取程序名称。*/
    public String getAppName() {
        return this.getString("appSettings.appName");
    };
    /**获取程序名称。*/
    public String getDisplayName() {
        return this.getString("appSettings.displayName");
    };
    /**解析全局配置参数，并且返回其Boolean形式对象。*/
    public boolean getBoolean(String name) {
        return this.getGlobal().getBoolean(name, false);
    };
    /**解析全局配置参数，并且返回其Integer形式对象。*/
    public int getInteger(String name) {
        return this.getGlobal().getInteger(name, 0);
    };
    /**解析全局配置参数，并且返回其String形式对象。*/
    public String getString(String name) {
        return this.getGlobal().getString(name, "");
    };
    /**解析全局配置参数，并且返回其{@link File}形式对象（用于表示目录）。*/
    public String getDirectoryPath(String name) {
        return this.getGlobal().getDirectoryPath(name);
    };
}