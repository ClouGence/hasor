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
package org.more.hypha.anno.xml;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.more.core.event.Event;
import org.more.core.event.Event.Sequence;
import org.more.core.event.EventListener;
import org.more.core.log.Log;
import org.more.core.log.LogFactory;
import org.more.hypha.AbstractBeanDefine;
import org.more.hypha.anno.AnnoService;
import org.more.hypha.anno.BeginScanEvent;
import org.more.hypha.anno.EndScanEvent;
import org.more.hypha.context.xml.XmlDefineResource;
import org.more.hypha.context.xml.XmlLoadedEvent;
import org.more.hypha.context.xml.XmlLoadedEvent.XmlLoadedEvent_Params;
import org.more.util.ResourcesUtil;
import org.more.util.ResourcesUtil.ScanItem;
import org.more.util.ScanEvent;
/** 
 * 创建{@link TagListener}对象，该对象的目的是为了驱动类扫描程序扫描class，并且解析生成{@link AbstractBeanDefine}对象。
 * @version 2010-10-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagListener implements EventListener<XmlLoadedEvent> {
    private static Log  log         = LogFactory.getLog(TagListener.class);
    private String      packageText = null;
    private AnnoService annoService = null;
    /**创建{@link TagListener}对象。 */
    public TagListener(String packageText, AnnoService annoService) {
        if (packageText == null || packageText.equals(""))
            this.packageText = "*";
        else
            this.packageText = packageText;
        this.annoService = annoService;
    };
    /**处理注解解析。 */
    public void onEvent(final XmlLoadedEvent event, final Sequence sequence) throws Throwable {
        log.info("start ANNO at package '{%0}'", this.packageText);
        StringBuffer buffer = new StringBuffer(this.packageText.replace(".", "/"));
        //        if (buffer.charAt(0) != '/')
        //            buffer.insert(0, "/");//.deleteCharAt(0);
        if (buffer.charAt(0) == '/')
            buffer.deleteCharAt(0);
        XmlLoadedEvent_Params params = event.toParams(sequence);
        final XmlDefineResource config = params.xmlDefineResource;
        final AnnoService engine = this.annoService;
        //
        String scanPackage = buffer.toString();
        config.getEventManager().doEvent(Event.getEvent(BeginScanEvent.class), config, scanPackage, this.annoService);
        ResourcesUtil.scan(scanPackage, new ScanItem() {
            public boolean goFind(ScanEvent event, boolean isInJar) throws FileNotFoundException, IOException, ClassNotFoundException {
                //1.排除一切非Class数据
                if (event.getName().endsWith(".class") == false)
                    return false;
                //2.通知引擎扫描这个类，确定是否有必要解析。引擎会使用ASM进行扫描增加速度。
                engine.parserClass(event.getStream());
                return false;
            }
        });
        config.getEventManager().doEvent(Event.getEvent(EndScanEvent.class), config, scanPackage, this.annoService);
    }
};