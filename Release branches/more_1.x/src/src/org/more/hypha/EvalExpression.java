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
package org.more.hypha;
import org.more.util.attribute.IAttribute;
/**
 * EL表达式执行器，通过该接口可以执行el表示。并且得到el表达式的执行结果。
 * Date : 2011-4-13
 * @author 赵永春 (zyc@byshell.org)
 */
public interface EvalExpression extends IAttribute {
    /**获取这个el表达式的字符串形式。*/
    public String getExpressionString();
    /**执行el表达式，参数表示了在执行el表达式时候使用的this是谁。 */
    public Object eval(Object thisObject) throws Throwable;
};