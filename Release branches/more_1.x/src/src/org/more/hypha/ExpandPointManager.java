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
 * 该类负责管理并调用并且执行扩展点的基类。
 * @version 2011-1-14
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ExpandPointManager {
    /**
     * 顺序执行所有已注册的扩展点对象，直到执行完毕所有匹配类型的扩展点为止。
     * @param type 扩展点类型。
     * @param params 执行的参数。
     * @return 返回执行结果。
     */
    public Object exePointOnSequence(Class<? extends ExpandPoint> type, Object... params);
    /**
     * 顺序执行所有已注册的扩展点对象，当遇到一个返回值时结束执行扩展点，否则直到执行完毕所有扩展点返回。
     * @param type 扩展点类型。
     * @param params 执行的参数。
     * @return 返回执行结果。
     */
    public Object exePointOnReturn(Class<? extends ExpandPoint> type, Object... params);
    /** 注册一个可执行的扩展点，可以重复注册同一个扩展点。 */
    public void regeditExpandPoint(ExpandPoint point);
};