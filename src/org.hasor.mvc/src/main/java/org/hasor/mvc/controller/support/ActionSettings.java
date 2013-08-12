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
package org.hasor.mvc.controller.support;
import java.util.ArrayList;
import java.util.List;
import org.hasor.context.Settings;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2013-5-11
 * @author 赵永春 (zyc@byshell.org)
 */
class ActionSettings /*implements HasorSettingListener*/{
    /**模式：mode:RestOnly（rest风格）、ServletOnly（中央servlet）、Both（两者同时使用）*/
    public static final String ActionServlet_Mode            = "hasor-mvc.actionServlet.mode";
    /**action拦截器.*/
    public static final String ActionServlet_Intercept       = "hasor-mvc.actionServlet.intercept";
    /**默认产生的Mime-Type类型.*/
    public static final String ActionServlet_DefaultProduces = "hasor-mvc.actionServlet.defaultProduces";
    /**方法忽略的方法（逗号分割多组方法名），注意：在这里配置的忽略会应用到所有action上.*/
    public static final String ActionServlet_IgnoreMethod    = "hasor-mvc.actionServlet.ignoreMethod";
    //
    //
    /**Action功能的工作模式。*/
    public static enum ActionWorkMode {
        /**仅工作在rest风格下*/
        RestOnly,
        /**通过中央servlet转发*.do的请求。*/
        ServletOnly,
        /**同时工作在RestOnly、ServletOnly两个模式下。其中ServletOnly模式优先。*/
        Both
    }
    private String         intercept       = null; //action拦截器.
    private String         defaultProduces = null; //默认响应类型
    private ActionWorkMode mode            = null; //工作模式
    private List<String>   ignoreMethod    = null; //忽略的方法
    //
    public void onLoadConfig(Settings newConfig) {
        this.intercept = newConfig.getString(ActionServlet_Intercept, "*.do");
        this.defaultProduces = newConfig.getString(ActionServlet_DefaultProduces, null);
        this.mode = newConfig.getEnum(ActionServlet_Mode, ActionWorkMode.class, ActionWorkMode.ServletOnly);
        this.ignoreMethod = new ArrayList<String>();
        String[] ignoreStrArray = newConfig.getStringArray(ActionServlet_IgnoreMethod);
        if (ignoreStrArray != null) {
            for (String ignoreStr : ignoreStrArray) {
                if (StringUtils.isBlank(ignoreStr))
                    continue;
                String[] ignoreArray = ignoreStr.split(",");
                for (String str : ignoreArray) {
                    if (StringUtils.isBlank(str))
                        continue;
                    this.ignoreMethod.add(str.trim());
                }
            }
        }
    }
    public String getDefaultProduces() {
        return defaultProduces;
    }
    public String getIntercept() {
        return intercept;
    }
    public ActionWorkMode getMode() {
        return mode;
    }
    public List<String> getIgnoreMethod() {
        return ignoreMethod;
    }
}