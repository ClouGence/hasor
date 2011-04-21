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
package org.more.hypha.expandpoint;
import java.util.ArrayList;
import java.util.List;
import org.more.hypha.DefineResource;
import org.more.hypha.ExpandPoint;
import org.more.hypha.ExpandPointManager;
import org.more.util.attribute.IAttribute;
/**
 * 该类负责管理并调用并且执行扩展点的基类。
 * @version 2011-1-14
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractExpandPointManager implements ExpandPointManager {
    private DefineResource    defineResource = null;
    private IAttribute        flash          = null;
    private List<ExpandPoint> expandList     = new ArrayList<ExpandPoint>();
    /***/
    public AbstractExpandPointManager(DefineResource defineResource) {
        this.defineResource = defineResource;
    }
    public void init(IAttribute flash) throws Throwable {
        this.flash = flash;
    }
    /**返回{@link DefineResource}对象。*/
    protected DefineResource getDefineResource() {
        return this.defineResource;
    }
    /**返回{@link IAttribute}类型的FLASH。*/
    protected IAttribute getFlash() {
        return this.flash;
    }
    public Object exePointOnSequence(Class<? extends ExpandPoint> type, Object[] params) {
        Object returnObj = null;
        for (ExpandPoint ep : expandList)
            if (type.isInstance(ep) == true)
                returnObj = ep.doIt(returnObj, params);
        return returnObj;
    }
    public Object exePointOnReturn(Class<? extends ExpandPoint> type, Object[] params) {
        Object returnObj = null;
        for (ExpandPoint ep : expandList)
            if (type.isInstance(ep) == true) {
                returnObj = ep.doIt(returnObj, params);
                if (returnObj != null)
                    break;
            }
        return returnObj;
    }
    public void regeditExpandPoint(ExpandPoint point) {
        this.expandList.add(point);
    }
};