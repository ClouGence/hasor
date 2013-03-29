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
package org.more.appengine;
import java.io.File;
import java.net.URL;
/**
* 
* @version : 2013-1-10
* @author 赵永春 (zyc@byshell.org)
*/
public class AppEngine {
    private String     startUpConfig = null;
    private Settings   settings      = null;
    private WorkSpace  workSpace     = null;
    private AppContext appContext    = null;
    //    private Injector injector = null;
    //    public AppEngine() {}
    /**获取应用程序名称。*/
    public String getAppName() {
        return this.settings.getAppName();
    };
    //    /**获取程序所在目录（绝对路径）。*/
    //    public String getAppHome() {
    //        return this.getDirectoryPath("workspace.appHome");
    //    };
    /**获取应用程序的属性设置。*/
    public Settings getSettings() {
        return this.settings;
    };
    /**获取程序的工作空间信息*/
    public WorkSpace getWorkSpace() {
        if (this.workSpace == null)
            this.workSpace = new WorkSpace(this.getSettings());
        return this.workSpace;
    };
    public AppContext getContext() {
        return this.appContext;
    }
    //---------------------------------------------------------------
    public static Settings loadSettings(String settingsPath) {
        return null;
    }
    public static Settings loadSettings(File settingsFile) {
        return null;
    }
    public static Settings loadSettings(URL settingsURL) {
        return null;
    }
    public static Settings loadSettingsBody(String settingsBody) {
        return null;
    }
}