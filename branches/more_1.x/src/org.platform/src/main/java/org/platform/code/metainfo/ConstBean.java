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
package org.platform.code.metainfo;
/**
 * 
 * @version : 2013-2-16
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ConstBean {
    private ConstBean parent       = null;
    private String    constCode    = "New Const";
    private String    constVar     = "";
    private String    constExtData = "";
    //
    public ConstBean() {}
    public ConstBean(ConstBean parent) {
        this.parent = parent;
    }
    public ConstBean getParent() {
        return parent;
    }
    //
    public void setParent(ConstBean parent) {
        this.parent = parent;
    }
    public String getConstCode() {
        return constCode;
    }
    public void setConstCode(String constCode) {
        this.constCode = constCode;
    }
    public String getConstVar() {
        return constVar;
    }
    public void setConstVar(String constVar) {
        this.constVar = constVar;
    }
    public String getConstExtData() {
        return constExtData;
    }
    public void setConstExtData(String constExtData) {
        this.constExtData = constExtData;
    }
}