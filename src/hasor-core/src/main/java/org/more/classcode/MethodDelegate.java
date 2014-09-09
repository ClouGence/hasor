/*
 * Copyright 2008-2009 the original 赵永春(zyc@hasor.net).
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
package org.more.classcode;
import java.lang.reflect.Method;
/**
 * 当使用{@link ClassConfig}类的addDelegate方法来添加委托，添加的委托处理函数对象就是该接口对象。
 * 被委托的方法将会采用注册委托时传递的MethodDelegate接口对象作为回调对象。
 * @version 2010-9-3
 * @author 赵永春 (zyc@hasor.net)
 */
public interface MethodDelegate {
    /**
     * 新生成的类附加接口方法被调用时候激发该接口的方法，通过callMethod可以获得被调用的方法对象。
     * @param callMethod 被调用的函数。
     * @param target 调用委托方法的类对象。
     * @param params 当调用方法时方法的参数，如果没有参数传入则是一个空数组。
     * @return 返回方法执行结果，注意依照相关接口方法的返回值进行返回{@link ClassConfig}不会自动转换起格式。
     * 如果方法返回类型是java基本类型请务必按照附加的接口方法返回相关返回值否则将产生类型转换异常。
     * @throws InvokeException 当调用过程中发生的异常。
     */
    public Object invoke(Method callMethod, Object target, Object[] params) throws Throwable;
}