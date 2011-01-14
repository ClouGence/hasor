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
/**
 * 扩展点管理器，所有扩展点都需要注册在{@link ExpandPointManager}接口中。
 * @version 2011-1-14
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ExpandPointManager {
    /**
     * 执行扩展点方法，并且并且按照扩展点执行逻辑进行执行。当执行完毕之后返回执行结果。
     * @param type 要执行的扩展点类型。
     * @param params 在执行期间传递的参数。
     * @return 返回执行扩展点之后的执行结果。
     */
    public Object exePoint(Class<? extends ExpandPoint> type, Object[] params);
};