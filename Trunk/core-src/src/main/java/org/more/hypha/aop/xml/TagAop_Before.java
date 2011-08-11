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
package org.more.hypha.aop.xml;
import org.more.hypha.aop.define.PointcutType;
import org.more.hypha.context.xml.XmlDefineResource;
/**
 * 用于解析aop:before标签
 * @version 2010-10-10
 * @author 赵永春 (zyc@byshell.org)
 */
public class TagAop_Before extends TagAop_AbstractListener {
    /**创建{@link TagAop_Before}对象*/
    public TagAop_Before(XmlDefineResource configuration) {
        super(configuration);
    }
    /**返回{@link PointcutType#Before}*/
    protected PointcutType getPointcutType() {
        return PointcutType.Before;
    }
}