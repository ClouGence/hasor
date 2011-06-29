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
package org.more.hypha.commons.point_support;
import java.util.List;
import org.more.hypha.Event.Sequence;
import org.more.hypha.EventListener;
import org.more.hypha.ExpandPointManager;
import org.more.hypha.PointFilter;
import org.more.hypha.commons.point_support.xml.Point_NS;
import org.more.hypha.context.AbstractApplicationContext;
import org.more.hypha.context.InitEvent;
import org.more.log.ILog;
import org.more.log.LogFactory;
/**
 * point的初始化。
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
class OnInit implements EventListener<InitEvent> {
    private static ILog log = LogFactory.getLog(OnInit.class);
    public void onEvent(InitEvent event, Sequence sequence) throws Throwable {
        AbstractApplicationContext context = (AbstractApplicationContext) event.toParams(sequence).applicationContext;
        ClassLoader loader = context.getClassLoader();
        ExpandPointManager pointManager = context.getExpandPointManager();
        //3.注册元信息解析器
        List<B_Point> elList = (List<B_Point>) context.getFlash().getAttribute(Point_NS.PointConfigList);
        if (elList != null)
            for (B_Point el : elList) {
                Class<?> elClass = loader.loadClass(el.getClassName());
                PointFilter point = (PointFilter) elClass.newInstance();
                pointManager.regeditExpandPoint(el.getName(), point);
            }
        log.info("hypha.point init OK!");
    };
}