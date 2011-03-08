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
import java.io.IOException;
import org.more.hypha.ApplicationContext;
import org.more.hypha.ExpandPoint;
import org.more.hypha.beans.AbstractBeanDefine;
/**
 * 字节码信息获取扩展点：该扩展点位于<b>类型创建或获取阶段</b>。该扩展点可以让外部程序有控制Bean字节码的全力，重而可以在字节码级别上完成对类的修改。
 * <br/>注意：1.如果挂载了多个{@link ClassByteExpandPoint}扩展点，则扩展点将被依次执行。并且每次执行之后的新字节码数据会被传入第二个扩展点。
 * <br/>注意：2.假如扩展点返回了一个null，那么在下一个扩展点中将不会得到来自于上一个扩展点的字节码数据。
 * <br/>扩展点顺序：<i><b>{@link ClassByteExpandPoint}</b></i>-&gt{@link ClassTypeExpandPoint}-&gt{@link BeforeCreateExpandPoint}-&gt{@link AfterCreateExpandPoint}
 * @version 2011-3-7
 * @author 赵永春 (zyc@byshell.org)
 */
public interface ClassByteExpandPoint extends ExpandPoint {
    /**
     * 执行扩展方法。
     * @param beanBytes 系统通过{@link AbstractBeanBuilder}或者来自于上一个{@link ClassByteExpandPoint}扩展点的字节码数据。
     * @param define 当前所处的bean定义对象。
     * @param context 扩展点所处的上下文。
     * @return 返回扩展点执行结果。
     * @throws IOException 在执行期间可能出现的异常。
     */
    public byte[] decorateBytes(byte[] beanBytes, AbstractBeanDefine define, ApplicationContext context) throws IOException;
};