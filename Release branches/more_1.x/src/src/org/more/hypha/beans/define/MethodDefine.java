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
package org.more.hypha.beans.define;
import java.util.ArrayList;
import org.more.hypha.AbstractDefine;
import org.more.hypha.beans.AbstractMethodDefine;
import org.more.hypha.beans.AbstractPropertyDefine;
/**
 * 该类是表示一个方法的描述，它实现了{@link AbstractMethodDefine}。
 * @version 2010-10-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class MethodDefine extends AbstractDefine implements AbstractMethodDefine {
    private String                 name     = null;
    private String                 codeName = null;
    private ArrayList<ParamDefine> params   = new ArrayList<ParamDefine>(); //属性
    /**返回方法的代理名称，代理名称是用于索引方法的目的。*/
    public String getName() {
        return this.name;
    };
    /**返回方法的真实名称，该属性是表示方法的真实方法名。*/
    public String getCodeName() {
        return this.codeName;
    };
    /**返回方法的参数列表描述。*/
    public AbstractPropertyDefine[] getParams() {
        AbstractPropertyDefine[] define = new AbstractPropertyDefine[this.params.size()];
        this.params.toArray(define);
        return define;
    }
    /**添加参数*/
    public void addParam(ParamDefine param) {
        this.params.add(param);
    }
    /**设置name属性*/
    public void setName(String name) {
        this.name = name;
    }
    /**设置codeName属性*/
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
}