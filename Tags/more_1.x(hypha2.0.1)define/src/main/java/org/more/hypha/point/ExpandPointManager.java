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
package org.more.hypha.point;
/**
 * 该类负责管理并调用并且执行扩展点的基类。
 * @version : 2011-6-29
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ExpandPointManager {
    /**
     * 顺序扩展点对象。
     * @param pointType 执行的扩展点类型
     * @param callBack 执行的最终回调函数
     * @param vars 参数对象
     * @return 返回执行扩展点的结果。
     */
    public <O> O exePoint(Class<? extends PointFilter> pointType, PointCallBack callBack, Object... vars) throws Throwable;
    /** 注册一个扩展点,第一个参数是注册的扩展点名，可以通过enablePoint和disablePoint方法启用禁用注册的扩展点。注册的名称不允许重名。 */
    public void regeditExpandPoint(String pointName, PointFilter point);
    /**启用扩展点。*/
    public void enablePoint(String pointName);
    /**禁用扩展点。*/
    public void disablePoint(String pointName);
};