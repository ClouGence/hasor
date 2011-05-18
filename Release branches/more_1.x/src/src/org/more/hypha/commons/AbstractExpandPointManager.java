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
package org.more.hypha.commons;
import java.util.ArrayList;
import java.util.List;
import org.more.hypha.ExpandPoint;
import org.more.hypha.ExpandPointManager;
import org.more.hypha.context.AbstractDefineResource;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * 该类负责管理并调用并且执行扩展点的基类。
 * @version 2011-1-14
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class AbstractExpandPointManager implements ExpandPointManager {
    private static ILog            log            = LogFactory.getLog(AbstractExpandPointManager.class);
    private AbstractDefineResource defineResource = null;
    private List<ExpandPoint>      expandList     = new ArrayList<ExpandPoint>();
    /*------------------------------------------------------------------------------*/
    public void init(AbstractDefineResource defineResource) {
        if (defineResource != null)
            log.info("init ExpandPointManager, AbstractDefineResource = {%0}", defineResource);
        else
            log.warning("init ExpandPointManager, AbstractDefineResource is null.");
        this.defineResource = defineResource;
    };
    /**返回{@link AbstractDefineResource}对象。*/
    protected AbstractDefineResource getDefineResource() {
        return this.defineResource;
    };
    /*------------------------------------------------------------------------------*/
    public Object exePointOnSequence(Class<? extends ExpandPoint> type, Object... params) {
        Object returnObj = null;
        for (ExpandPoint ep : expandList)
            if (type.isInstance(ep) == true) {
                returnObj = ep.doIt(returnObj, params);
                log.info("run ExpandPoint OnSequence ,point = {%0} ,params = {%1} , return = {%2}", ep, params, returnObj);
            }
        return returnObj;
    };
    public Object exePointOnReturn(Class<? extends ExpandPoint> type, Object... params) {
        Object returnObj = null;
        for (ExpandPoint ep : expandList)
            if (type.isInstance(ep) == true) {
                returnObj = ep.doIt(returnObj, params);
                log.info("run ExpandPoint OnReturn ,point = {%0} ,params = {%1} , return = {%2}", ep, params, returnObj);
                if (returnObj != null)
                    break;
            }
        return returnObj;
    };
    public void regeditExpandPoint(ExpandPoint point) {
        if (point == null) {
            log.warning("regedit ExpandPoint an error ExpandPoint is null.");
            return;
        }
        log.info("regedit ExpandPoint {%0}.", point);
        this.expandList.add(point);
    };
};