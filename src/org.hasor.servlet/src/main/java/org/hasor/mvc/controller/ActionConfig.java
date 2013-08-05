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
package org.hasor.mvc.controller;
/**
 * 
 * @version : 2013-5-28
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ActionConfig {
    /**是否启用Action功能.*/
    public static final String ActionServlet_Enable          = "hasor-mvc.actionServlet.enable";
    /**模式：mode:RestOnly（rest风格）、ServletOnly（中央servlet）、Both（两者同时使用）*/
    public static final String ActionServlet_Mode            = "hasor-mvc.actionServlet.mode";
    /**action拦截器.*/
    public static final String ActionServlet_Intercept       = "hasor-mvc.actionServlet.intercept";
    /**默认产生的Mime-Type类型.*/
    public static final String ActionServlet_DefaultProduces = "hasor-mvc.actionServlet.defaultProduces";
    /**方法忽略的方法（逗号分割多组方法名），注意：在这里配置的忽略会应用到所有action上.*/
    public static final String ActionServlet_IgnoreMethod    = "hasor-mvc.actionServlet.ignoreMethod";
}