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
package org.more.hypha.anno.assembler;
import org.more.hypha.anno.define.Bean;
/**
 * MetaData工具类
 * @version : 2011-5-24
 * @author 赵永春 (zyc@byshell.org)
 */
public class AnnoMetaDataUtil {
    /**确定注解形式下的Bean名称*/
    public static String getBeanID(Bean annoBean, Class<?> beanType) {
        String var = annoBean.id();
        if (var.equals("") == true) {
            var = annoBean.logicPackage();
            if (var.equals("") == false)
                var += ("." + annoBean.name());
            else
                var = annoBean.name();
        }
        if (var.equals("") == true)
            var = beanType.getName();
        return var;
    };
}