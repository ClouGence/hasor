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
package org.more.beans.define;
/**
 * 注入类型枚举，定义了Export、Fact、Ioc、EL、Auto五种属性注入方式。<br/><br/>
 * <b>Export方式：</b>使用Export方式可以使开发人员参与属性注入过程，在Export方式下more.beans不会对属性做任何注入操作。
 * more.bean会委托{@link ExportIoc}接口进行注入请求。决定对属性的注入类必须实现{@link ExportIoc}接口。
 * 如果外部注入处理对象为空则Export将忽略注入请求。<br/>
 * <b>Fact方式：</b>在Fact方式下首次请求装载类时more.beans会生成一个属性注入器，并且使用这个属性注入类进行注入。
 * 属性注入器会使用get/set方式进行注入。从而忽略了反射注入的性能消耗过程。对于基本类型而言该方式大大的提升了基本类型属性的注入时间。<br/>
 * <b>Ioc方式：</b>传统的注入方式，使用java.lang.reflect包中的类进行反射调用来实现依赖注入。<br/>
 * <b>EL方式：</b>这是beans2.0新增的一种注入方式，该属性的 注入使用的是Ognl软件包来实现。<br/>
 * <b>Auto方式(默认)：</b>这是默认的注入方式，系统会根据需要自动选择上述4种注入方式。
 * @version 2010-9-18
 * @author 赵永春 (zyc@byshell.org)
 */
public enum IocTypeEnum {
    /**导出注入的过程到{@link ExportIoc}接口中注入代码由开发人员手动完成。*/
    Export,
    /**快速注入，快速注入会生成一个快速注入类通过这个类执行注入过程。*/
    Fact,
    /**传统的注入方式，使用java.lang.reflect包中的类进行反射调用来实现依赖注入。*/
    Ioc,
    /**使用的是Ognl表达式语言实现注入。*/
    EL,
    /**自动选择注入形式。*/
    Auto,
}