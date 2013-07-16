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
package org.more.classcode;
/**
 * 策略基接口，该接口中定义了初始化策略和重置策略两个方法。每当{@link ClassEngine#builderClass()}方法被调用时
 * 都会先初始化所有策略，按后在生成类之后重置它。
 * @version 2010-9-3
 * @author 赵永春 (zyc@byshell.org)
 */
interface BaseStrategy {
    /**初始化策略。*/
    public void initStrategy(ClassEngine classEngine);
}