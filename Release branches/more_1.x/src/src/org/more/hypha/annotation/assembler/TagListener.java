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
package org.more.hypha.annotation.assembler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.more.InitializationException;
import org.more.hypha.DefineResource;
import org.more.hypha.Event;
import org.more.hypha.EventListener;
import org.more.hypha.annotation.AnnoResourcePlugin;
import org.more.hypha.beans.AbstractBeanDefine;
import org.more.hypha.event.Config_LoadedXmlEvent;
import org.more.util.ClassPathUtil;
import org.more.util.ClassPathUtil.ScanItem;
import org.more.util.ScanEvent;
/** 
 * 创建{@link TagListener}对象，该对象的目的是为了驱动类扫描程序扫描class，并且解析生成{@link AbstractBeanDefine}对象。
 * @version 2010-10-13
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagListener implements EventListener {
    private String packageText = null;
    /**创建{@link TagListener}对象。*/
    public TagListener(String packageText) {
        if (packageText == null || packageText.equals(""))
            this.packageText = "*";
        else
            this.packageText = packageText;
    };
    /**处理注解解析。*/
    public void onEvent(final Event event) {
        final Config_LoadedXmlEvent eve = (Config_LoadedXmlEvent) event;
        System.out.println("start ANNO at package:" + this.packageText);
        StringBuffer buffer = new StringBuffer(this.packageText.replace(".", "/"));
        if (buffer.charAt(0) != '/')
            buffer.insert(0, '/');
        try {
            final DefineResource config = eve.getResource();
            final AnnoResourcePlugin engine = (AnnoResourcePlugin) config.getPlugin(AnnoResourcePlugin.AnnoDefineResourcePluginName);
            //
            ClassPathUtil.scan(buffer.toString(), new ScanItem() {
                public boolean goFind(ScanEvent event, boolean isInJar, File context) throws FileNotFoundException, IOException, ClassNotFoundException {
                    //1.排除一切非Class数据
                    if (event.getName().endsWith(".class") == false)
                        return false;
                    //2.通知引擎扫描这个类，确定是否有必要解析。引擎会使用ASM进行扫描增加速度。
                    engine.parserClass(event.getStream());
                    return false;
                }
            });
        } catch (Throwable e) {
            throw new InitializationException(e.getMessage(), e);
        }
    };
};