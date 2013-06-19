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
package org.more.webui.context;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import org.more.util.StringConvertUtil;
import org.more.webui.support.UIComponent;
import org.more.webui.web.DefaultWebUIFactory;
/**
 * 该类负责创建webui的各类组建以及获取相关需要配置。
 * @version : 2012-5-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class FacesConfig {
    public static enum WebUIConfig {
        /**页面模板输出编码，默认utf-8。*/
        OutEncoding("WebUI_OutEncoding"),
        /**页面字符编码，默认utf-8。*/
        PageEncoding("WebUI_PageEncoding"),
        /**国际化的支持，默认false。*/
        LocalizedLookup("WebUI_Localized"),
        /**拦截的扩展名，默认.xhtml*/
        FacesSuffix("WebUI_Faces"),
        /**{@link WebUIFactory}接口实现类，默认：DefaultWebUIFactory。*/
        FactoryName("WebUI_FactoryName"),
        /**扫描的包，默认:org.*,com.*,net.*,java.**/
        ScanPackages("WebUI_ScanPackages"), ;
        //
        private String value = null;
        WebUIConfig(String value) {
            this.value = value;
        }
        public String value() {
            return this.value;
        }
    }
    private FilterConfig initConfig = null;
    public FacesConfig(FilterConfig initConfig) {
        this.initConfig = initConfig;
    }
    private static long genID = 0;
    /**根据组件类型，生成个组件ID*/
    public static String generateID(Class<? extends UIComponent> compClass) {
        return "com_" + (genID++);
    }
    /**获取初始化的环境参数。*/
    public String getInitConfig(String key) {
        return this.initConfig.getInitParameter(key);
    }
    /**获取ServletContext*/
    public ServletContext getServletContext() {
        return this.initConfig.getServletContext();
    }
    /*--------------------------------------------------------------------------*/
    private String factoryName = null;
    public String getWebUIFactoryClass() {
        if (this.factoryName == null)
            this.factoryName = StringConvertUtil.parseString(this.getInitConfig(WebUIConfig.FactoryName.value()), DefaultWebUIFactory.class.getName());
        return this.factoryName;
    };
    //
    private Boolean localizedLookup = null;
    /**获取一个boolean值该值决定了模板是否支持国际化。*/
    public boolean isLocalizedLookup() {
        if (this.localizedLookup == null)
            this.localizedLookup = StringConvertUtil.parseBoolean(this.getInitConfig(WebUIConfig.LocalizedLookup.value()), false);
        return localizedLookup;
    };
    //
    private String outEncoding = null;
    /**输出编码*/
    public String getOutEncoding() {
        if (this.outEncoding == null)
            this.outEncoding = StringConvertUtil.parseString(this.getInitConfig(WebUIConfig.OutEncoding.value()), "utf-8");
        return this.outEncoding;
    };
    //
    private String pageEncoding = null;
    /**获取页面使用的字符编码*/
    public String getPageEncoding() {
        if (this.pageEncoding == null)
            this.pageEncoding = StringConvertUtil.parseString(this.getInitConfig(WebUIConfig.PageEncoding.value()), "utf-8");
        return this.pageEncoding;
    };
    //
    private String facesSuffix = null;
    /**获取一个扩展名，凡是具备该扩展名的文件都被视为UI文件。*/
    public String getFacesSuffix() {
        if (this.facesSuffix == null)
            this.facesSuffix = StringConvertUtil.parseString(this.getInitConfig(WebUIConfig.FacesSuffix.value()), ".xhtml");
        return this.facesSuffix;
    };
    //
    private String scanPackages = null;
    /**扫描的包，默认:org.*,com.*,net.*,java.* */
    public String getScanPackages() {
        if (this.scanPackages == null)
            this.scanPackages = StringConvertUtil.parseString(this.getInitConfig(WebUIConfig.ScanPackages.value()), "org.*,com.*,net.*,java.*");
        return this.scanPackages;
    };
}