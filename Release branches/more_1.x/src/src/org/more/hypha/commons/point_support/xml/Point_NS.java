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
package org.more.hypha.commons.point_support.xml;
import org.more.hypha.commons.xml.Tag_Abstract;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 用于解析el-config命名空间标签解析器基类，主要用于区分不同命名空间。
 * @version : 2011-4-22
 * @author 赵永春 (zyc@byshell.org)
 */
public abstract class Point_NS extends Tag_Abstract {
    public static final String PointConfigList = "org.more.hypha.point.Point_LIST";
    public Point_NS(XmlDefineResource configuration) {
        super(configuration);
    };
};