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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import org.more.core.error.RepeateException;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.AbstractMethodDefine;
import org.more.hypha.commons.define.AbstractDefine;
/**
 * 该类是表示一个方法的描述，它实现了{@link AbstractMethodDefine}。
 * @version 2010-10-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class MethodDefine extends AbstractDefine<AbstractMethodDefine> implements AbstractMethodDefine {
    private String                 name          = null;
    private String                 codeName      = null;
    private ArrayList<ParamDefine> params        = new ArrayList<ParamDefine>(); //属性
    private boolean                boolStatic    = false;
    private AbstractBeanDefine     forBeanDefine = null;
    /**创建{@link MethodDefine}类型对象，参数表明该方法的所属bean定义。*/
    public MethodDefine(AbstractBeanDefine forBeanDefine) {
        this.forBeanDefine = forBeanDefine;
    }
    /**用于返回一个boolean值，该值表明位于bean上的方法是否为一个静态方法。*/
    public boolean isStatic() {
        return this.boolStatic;
    }
    /**获取这个方法所属的bean定义*/
    public AbstractBeanDefine getForBeanDefine() {
        return this.forBeanDefine;
    }
    /**返回方法的代理名称，代理名称是用于索引方法的目的。*/
    public String getName() {
        return this.name;
    };
    /**返回方法的真实名称，该属性是表示方法的真实方法名。*/
    public String getCodeName() {
        return this.codeName;
    };
    /**返回方法的参数列表描述，返回的集合是只读的。*/
    public Collection<? extends ParamDefine> getParams() {
        return Collections.unmodifiableCollection(this.params);
    }
    /**添加参数*/
    public void addParam(ParamDefine param) {
        this.params.add(param);
        final MethodDefine define = this;
        Collections.sort(this.params, new Comparator<ParamDefine>() {
            public int compare(ParamDefine arg0, ParamDefine arg1) {
                int cdefine_1 = arg0.getIndex();
                int cdefine_2 = arg1.getIndex();
                if (cdefine_1 > cdefine_2)
                    return 1;
                else if (cdefine_1 < cdefine_2)
                    return -1;
                else
                    throw new RepeateException(define + "[" + arg0 + "]与[" + arg1 + "]方法参数索引重复.");
            }
        });
    }
    /**设置name属性*/
    public void setName(String name) {
        this.name = name;
    }
    /**设置codeName属性*/
    public void setCodeName(String codeName) {
        this.codeName = codeName;
    }
    /**设置该方法是否为一个静态方法。*/
    public void setBoolStatic(boolean boolStatic) {
        this.boolStatic = boolStatic;
    }
}