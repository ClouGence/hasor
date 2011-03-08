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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * {@link ExpandPointManager}接口的一个抽象实现，该类负责调用并且执行扩展点。
 * @version 2011-1-14
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractExpandPointManager {
    private Map<Class<?>, List<ExpandPoint>> expandMap  = new HashMap<Class<?>, List<ExpandPoint>>();
    private List<ExpandPoint>                expandList = new ArrayList<ExpandPoint>();
    /**
     * 
     * @param type 扩展点类型。
     * @param context 扩展点上下文。即在哪个对象上执行的扩展点，那个对象就是扩展点上下文。
     * @param params 执行的扩展点所传入的其他参数。
     * @return 返回扩展点执行结果。
     */
    public Object exePoint(Class<? extends ExpandPoint> type, Object context, Object[] params) {
        // TODO Auto-generated method stub
        return null;
    }
};