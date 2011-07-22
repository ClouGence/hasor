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
package org.more.hypha.beans.assembler;
import org.more.core.log.ILog;
import org.more.hypha.ApplicationContext;
import org.more.hypha.beans.define.Collection_ValueMetaData;
import org.more.hypha.commons.define.AbstractDefine;
import org.more.util.attribute.IAttribute;
/**
 * MetaData工具类
 * @version : 2011-5-24
 * @author 赵永春 (zyc@byshell.org)
 */
public class MetaDataUtil {
    private static final String FungiCacheName = "$FungiCacheName_Type";
    public static Class<?> pass(Collection_ValueMetaData<?> vmd, ApplicationContext context, ILog log, Class<?> defaultCollection) throws ClassNotFoundException {
        //1.确定类型
        IAttribute fungiAtt = vmd.getFungi();
        Class<?> listType = (Class<?>) vmd.getFungi().getAttribute(FungiCacheName);
        if (listType == null) {
            String listTypeString = vmd.getCollectionType();
            if (listTypeString != null) {
                listType = (Class<?>) context.getClassLoader().loadClass(listTypeString);
                log.debug("return load {%0} type.", listType);
            }
            if (listType == null) {
                listType = defaultCollection;
                log.debug("return default type {%0}.", listType);
            }
            fungiAtt.setAttribute(FungiCacheName, listType);
        } else
            log.debug("return {%0} from fungi.", listType);
        return listType;
    };
    /**从缓存中获取解析的类型。 */
    public static Class<?> getTypeForFungi(AbstractDefine<?> data, ILog log) {
        IAttribute fungiAtt = data.getFungi();
        if (fungiAtt.contains(FungiCacheName) == true) {
            Class<?> setType = (Class<?>) fungiAtt.getAttribute(FungiCacheName);
            log.debug("return from fungi cache ,cacheName is {%0}, type = {%1}", FungiCacheName, setType);
            return setType;
        }
        log.debug("cache is null.");
        return null;
    };
    /**缓存类型*/
    public static void putTypeToFungi(AbstractDefine<?> data, Class<?> type, ILog log) {
        IAttribute fungiAtt = data.getFungi();
        log.debug("cache BeanBuilder = {%0}, type = {%1}.", data, type);
        fungiAtt.setAttribute(FungiCacheName, type);
    };
}