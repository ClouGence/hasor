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
package net.hasor.core.context.factorys;
import java.util.List;
import net.hasor.core.BindInfoFactory;
import net.hasor.core.BindInfoFactoryCreater;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import org.more.UndefinedException;
import org.more.util.StringUtils;
/**
 * 
 * @version : 2014-5-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class DefaultBindInfoFactoryCreater implements BindInfoFactoryCreater {
    @Override
    public BindInfoFactory create(final Environment env) {
        String createrToUse = null;
        //1.取得即将创建的ManagerCreater类型
        Settings setting = env.getSettings();
        String defaultManager = setting.getString("hasor.bindFactory.default");
        XmlNode[] managerArray = setting.getXmlNodeArray("hasor.bindFactory");
        for (XmlNode manager : managerArray) {
            List<XmlNode> createrList = manager.getChildren("creater");
            for (XmlNode creater : createrList) {
                String createrName = creater.getAttribute("name");
                if (StringUtils.equalsIgnoreCase(createrName, defaultManager)) {
                    createrToUse = creater.getAttribute("class");
                    break;
                }
            }
            if (createrToUse != null) {
                break;
            }
        }
        //2.排错
        if (createrToUse == null) {
            throw new UndefinedException(String.format("%s is not define.", defaultManager));
        }
        //3.创建Creater
        try {
            Class<?> createrType = Thread.currentThread().getContextClassLoader().loadClass(createrToUse);
            BindInfoFactoryCreater creater = (BindInfoFactoryCreater) createrType.newInstance();
            return creater.create(env);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }
}