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
package org.platform.view.freemarker;
import org.more.webui.freemarker.loader.ConfigTemplateLoader;
import org.platform.context.AppContext;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
/**
 * 
 * @version : 2013-5-16
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ConfigurationFactory {
    /**是否启用freemarker.*/
    public static final String FreemarkerConfig_Enable               = "freemarker.enable";
    /**freemarker模板后缀名逗号间隔多个.*/
    public static final String FreemarkerConfig_Suffix               = "freemarker.suffixSet";
    /**当模板处理发生异常时的处理方式.*/
    public static final String FreemarkerConfig_OnError              = "freemarker.onError";
    /**Configuration对象创建工厂.*/
    public static final String FreemarkerConfig_ConfigurationFactory = "freemarker.configurationFactory";
    /**FreeMarker配置.*/
    public static final String FreemarkerConfig_Settings             = "freemarker.settings";
    /**FreeMarker装载器配置.*/
    public static final String FreemarkerConfig_TemplateLoader       = "freemarker.templateLoader";
    //
    /*** 获取配置好的freemarker{@link Configuration}对象。*/
    public Configuration configuration(AppContext appContext);
    /***/
    public TemplateLoader createTemplateLoader(AppContext appContext);
    //
    public ConfigTemplateLoader createConfigTemplateLoader(AppContext appContext);
}