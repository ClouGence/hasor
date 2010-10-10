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
/**
 * 该类型aop定义可以定义一个无参数的方法作为aop接收调用方法，aop状态数据需要通过aop运行时获取。
 * 该类型不支持{@link PointcutType#Filter}形式的aop。
 * @version 2010-9-27
 * @author 赵永春 (zyc@byshell.org)
 */
public class AopMethodInformed extends AopDefineInformed {
    private String method = null; //定义的方法名
    /**获取定义的方法名。*/
    public String getMethod() {
        return this.method;
    }
    /**设置定义的方法名。*/
    public void setMethod(String method) {
        this.method = method;
    }
}