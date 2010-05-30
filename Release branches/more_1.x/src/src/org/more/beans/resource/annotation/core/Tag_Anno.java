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
package org.more.beans.resource.annotation.core;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLStreamReader;
import org.more.beans.resource.annotation.util.AnnoEngine;
import org.more.beans.resource.annotation.util.PackageUtil;
import org.more.beans.resource.annotation.util.PackageUtilExclude;
import org.more.beans.resource.xml.TagProcess;
import org.more.beans.resource.xml.XmlContextStack;
import org.more.util.StringConvert;
/**
* 负责扫描所有bean定义名称。
* @version 2010-1-10
* @author 赵永春 (zyc@byshell.org)
*/
public class Tag_Anno extends TagProcess {
    private boolean                 xml_enable  = true; //配置文件决定是否处于启用状态。
    private String                  xml_package = ".*"; //
    private boolean                 isLock      = false; //只有isLock锁标记为锁定，并且已经执行过初始化过程。锁才真正生效。
    private HashMap<String, String> names;              //用于保存扫描到的bean名和类名。
    private LinkedList<String>      initNames;          //用于保存扫描到的bean名和类名。
    //=========================================================================================Job
    public void lockScan() {
        this.isLock = true;
    }
    public void unLockScan() {
        this.isLock = false;
    }
    @Override
    public void doStartEvent(String xPath, XMLStreamReader xmlReader, XmlContextStack context) throws IOException {
        /*只有isLock锁标记为锁定，并且已经执行过初始化过程。锁才真正生效-------*/
        if (isLock == true && this.names != null)
            return;
        /*判断是否是当前程序可以处理的标签-----------------------------------*/
        String tagPrefix = context.getTagPrefix();
        String tagName = context.getTagName();
        if ("anno".equals(tagPrefix) == false || "anno".equals(tagName) == false)
            return;
        /*读取属性anno标签的属性--------------------------------------------*/
        int attCount = xmlReader.getAttributeCount();
        for (int i = 0; i < attCount; i++) {
            String key = xmlReader.getAttributeLocalName(i);
            if (key.equals("enable") == true)
                xml_enable = StringConvert.parseBoolean(xmlReader.getAttributeValue(i), true);
            else if (key.equals("package") == true) {
                xml_package = xmlReader.getAttributeValue(i);
                xml_package = (xml_package == null) ? "*" : xml_package.replace(",", ")|(");
                //替换字符以实现支持 “*”，“?”通配符。
                xml_package = xml_package.replace(".", "\\.");
                xml_package = xml_package.replace("*", ".*");
                xml_package = xml_package.replace("?", ".");
                xml_package = "(" + xml_package + ")";
            }
        }
        if (xml_enable == false)
            return;
        /*扫描名称----------------------------------------------------------*/
        this.names = new HashMap<String, String>();//初始化names属性
        this.initNames = new LinkedList<String>();
        //取得所有处于包含路径下的类文件以及资源文件的列表
        PackageUtil util = new PackageUtil();
        final String _package = xml_package;
        LinkedList<String> classNames = util.scanClassPath(new PackageUtilExclude() {
            @Override
            public boolean exclude(String name) {
                return !name.matches(_package);
            }
        });
        //扫描每一个被选中的资源
        AnnoEngine ae = new AnnoEngine();
        Scan_ClassName scanName = new Scan_ClassName();
        for (String className : classNames) {
            try {
                scanName.reset();
                ae.runTask(Class.forName(className), scanName, null);
                if (scanName.isBean() == true) {
                    this.names.put(scanName.getBeanName(), className);
                    if (scanName.isInit() == false)
                        this.initNames.add(scanName.getBeanName());
                }
            } catch (Exception e) {}
        }
    }
    public Map<String, String> getScanBeansResult() {
        return this.names;
    }
    public void destroy() {
        this.unLockScan();
        if (this.names != null)
            this.names.clear();
        this.names = null;
        if (this.initNames != null)
            this.initNames.clear();
        this.initNames = null;
    }
    public List<String> getScanInitBeansResult() {
        return initNames;
    }
}