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
package org.more.hypha.aop.define;
import java.lang.reflect.Method;
import org.more.hypha.AbstractDefine;
/**
 * 切入点，该类的职责是负责对类或方法进行匹配。
 * @version 2010-9-25
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractPointcutDefine extends AbstractDefine<AbstractPointcutDefine> {
    private String name = null;
    /**获取切入点名称*/
    public String getName() {
        return name;
    };
    /**匹配一个方法是否符合表达式的要求。*/
    public abstract boolean isMatch(Method method);
    /**设置切点名*/
    public void setName(String name) {
        this.name = name;
    };
};