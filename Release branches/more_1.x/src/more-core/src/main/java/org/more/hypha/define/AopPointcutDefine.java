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
package org.more.hypha.define;
import java.lang.reflect.Method;
/**
 * 切入点定义
 * @version 2010-9-24
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopPointcutDefine extends AopAbstractPointcutDefine {
    private String expression = null;
    /**获取匹配字符串。*/
    public String getExpression() {
        return this.expression;
    };
    /**设置表达式*/
    public void setExpression(String expression) {
        this.expression = expression;
    };
    /**测试方法是否被包含在切入点*/
    public boolean isMatch(Method method) {
        //TODO 实现匹配 
        return true;
    };
};