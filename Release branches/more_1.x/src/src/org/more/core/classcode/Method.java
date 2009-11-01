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
package org.more.core.classcode;
/**
 * 生成的类如果有附加接口实现则附加实现的接口委托方法保存在该对象上。在生成的新类中会有一个public的Map类型的字段。
 * 该字段中保存了所有附加接口实现方法的代理对象信息。而这个信息对象就是Method类。通常该对象只有生成的类中附加接口
 * 调用委托时才使用。在开发过程中开发人员不会碰触该类实例对象。
 * Date : 2009-10-19
 * @author 赵永春
 */
public class Method {
    /** 索引方法的Map UUID值。 */
    public String         uuid     = null;
    /** 映射的委托对象。 */
    public MethodDelegate delegate = null;
}