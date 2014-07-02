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
package net.hasor.core.context._.rm.simple.schema;
import java.util.ArrayList;
import java.util.List;
/**
 * 该接口用于定义{@link BeanDefine}上的一个方法。
 * @version 2010-10-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class MethodDefine extends AbstractDefine {
    /*静态方法标记*/
    private boolean           staticMark = false;
    /*定义的方法名（内部名称）.*/
    private String            name       = null;
    /*方法的真实名称.*/
    private String            codeName   = null;
    /*方法的返回值.*/
    private String            returnType = null;
    /*方法参数定义列表 */
    private List<ParamDefine> params     = new ArrayList<ParamDefine>();
    /*------------------------------------------------------------------*/
    /**获取静态方法标记*/
    public boolean isStaticMark() {
        return staticMark;
    }
    /**设置静态方法标记*/
    public void setStaticMark(boolean staticMark) {
        this.staticMark = staticMark;
    }
    /**获取定义的方法名（内部名称）.*/
    public String getName() {
        return name;
    }
    /**设置定义的方法名（内部名称）.*/
    public void setName(String name) {
        this.name = name;
    }
    /**获取方法的真实名称.*/
    public String getCodeName() {
        return codeName;
    }
    /**设置方法的真实名称.*/
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
    /**获取方法的返回值.*/
    public String getReturnType() {
        return returnType;
    }
    /**设置方法的返回值.*/
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }
    /**获取方法参数定义列表 */
    public List<ParamDefine> getParams() {
        return params;
    }
    /**设置方法参数定义列表 */
    public void setParams(List<ParamDefine> params) {
        this.params = params;
    }
}