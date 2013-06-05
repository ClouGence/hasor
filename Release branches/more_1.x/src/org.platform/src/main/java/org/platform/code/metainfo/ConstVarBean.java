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
 * @version : 2012-2-20
 * @author ’‘”¿¥∫ (zyc@byshell.org)
 */
public class ConstVarBean {
    private ConstBean    targetConst = null;
    private ConstVarBean parent      = null;
    private String       varKey      = "New Key";
    private String       varVar      = "";
    private String       varExtData  = "";
    //
    //
    public ConstVarBean(ConstBean targetConst) {
        this(targetConst, null);
    }
    public ConstVarBean(ConstBean targetConst, ConstVarBean parent) {
        this.targetConst = targetConst;
        this.parent = parent;
    }
    public ConstBean getConst() {
        return targetConst;
    }
    public void setConst(ConstBean targetConst) {
        this.targetConst = targetConst;
    }
    public ConstVarBean getParent() {
        return parent;
    }
    public void setParent(ConstVarBean parent) {
        this.parent = parent;
    }
    public String getVarKey() {
        return varKey;
    }
    public void setVarKey(String varKey) {
        this.varKey = varKey;
    }
    public String getVarVar() {
        return varVar;
    }
    public void setVarVar(String varVar) {
        this.varVar = varVar;
    }
    public String getVarExtData() {
        return varExtData;
    }
    public void setVarExtData(String varExtData) {
        this.varExtData = varExtData;
    }
}