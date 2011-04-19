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
package org.more.hypha.define.beans;
import java.util.ArrayList;
/**
 * ScriptBeanDefine类用于定义一个脚本中的bean的引用。
 * @version 2010-9-15
 * @author 赵永春 (zyc@byshell.org)
 */
public class ScriptBeanDefine extends TemplateBeanDefine {
    private String            scriptText    = null;                   //脚本内容
    private String            sourcePath    = null;                   //脚本文件位置
    private String            language      = "JavaScript";           //脚本引擎名
    private ArrayList<String> implementList = new ArrayList<String>(); //该bean对应的接口名
    /**返回“ScriptBean”。*/
    public String getBeanType() {
        return "ScriptBean";
    }
    /**获取脚本内容*/
    public String getScriptText() {
        return scriptText;
    }
    /**设置脚本内容*/
    public void setScriptText(String scriptText) {
        this.scriptText = scriptText;
    }
    /**获取脚本文件位置*/
    public String getSourcePath() {
        return sourcePath;
    }
    /**设置脚本文件位置*/
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }
    /**获取脚本引擎名。*/
    public String getLanguage() {
        return language;
    }
    /**设置脚本引擎名。*/
    public void setLanguage(String language) {
        this.language = language;
    }
    /**获取脚本bean实现的接口。*/
    public String[] getImplementList() {
        return (String[]) implementList.toArray();
    }
    /**添加一个接口实现。*/
    public void addImplement(String faceName) {
        if (this.implementList.contains(faceName) == false)
            this.implementList.add(faceName);
    }
    /**删除一个接口实现。*/
    public void removeImplement(String faceName) {
        if (this.implementList.contains(faceName) == true)
            this.implementList.remove(faceName);
    }
}