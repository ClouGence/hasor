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
package org.more.util.attribute;
/**
 *    自由的属性访问器。该接口进一步扩展了ExtAttribute接口，并支持属性替换模式的更换操作。
 * 使得在使用属性的过程中可以自由的切换属性替换策略。
 * Date : 2009-4-28
 * @author 赵永春
 */
public interface IFreeAttribute extends IExtAttribute {
    /** 将属性替换策略切换到ReplaceMode_Replace模式 */
    public void changeReplace();
    /** 将属性替换策略切换到ReplaceMode_Original模式 */
    public void changeOriginal();
    /** 将属性替换策略切换到ReplaceMode_Throw模式 */
    public void changeThrow();
}
