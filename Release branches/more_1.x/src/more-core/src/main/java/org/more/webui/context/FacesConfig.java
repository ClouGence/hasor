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
import java.util.Set;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import org.more.util.ClassUtil;
import org.more.webui.support.UIComponent;
/**
 * 该类负责创建webui的各类组建以及获取相关需要配置。
 * @version : 2012-5-22
 * @author 赵永春 (zyc@byshell.org)
 */
public class FacesConfig {
    private String       encoding        = "utf-8";                //字符编码
    private boolean      localizedLookup = false;                  //是否启用国际化的额支持
    private String       facesSuffix     = ".xhtml";
    private FilterConfig initConfig      = null;
    private String[]     scanPackages    = { "org", "com", "net" }; //扫描的类包路径
    /*----------------------------------------------------------------*/
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
    /**获取一个boolean值该值决定了模板是否支持国际化。*/
    public boolean isLocalizedLookup() {
        return this.localizedLookup;
    }
    /**设置一个boolean值该值决定了模板是否支持国际化。*/
    public void setLocalizedLookup(boolean localizedLookup) {
        this.localizedLookup = localizedLookup;
    }
    /**获取页面使用的字符编码*/
    public String getEncoding() {
        return this.encoding;
    };
    /**设置页面使用的字符编码*/
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    /**获取一个扩展名，凡是具备该扩展名的文件都被视为UI文件。*/
    public String getFacesSuffix() {
        return this.facesSuffix;
    }
    /**设置一个扩展名，凡是具备该扩展名的文件都被视为UI文件。*/
    public void setFacesSuffix(String facesSuffix) {
        this.facesSuffix = facesSuffix;
    }
    /**获取扫描的类包路径*/
    public String[] getScanPackages() {
        return scanPackages;
    }
    /**设置扫描的类包路径*/
    public void setScanPackages(String[] scanPackages) {
        this.scanPackages = scanPackages;
    }
    /*----------------------------------------------------------------*/
    private ClassUtil classUtil = null;
    public Set<Class<?>> getClassSet(Class<?> targetType) {
        if (classUtil == null)
            this.classUtil = ClassUtil.newInstance(this.scanPackages);
        return classUtil.getClassSet(targetType);
    }
}