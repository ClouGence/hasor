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
package org.dev.toos.ui.internal.ui;
/**
 * 字段监听器，当字段的值发生改变之后触发。
 * @version : 2013-3-20
 * @author 赵永春 (zyc@byshell.org)
 */
public interface IViewFieldListener {
    /** 监视的字段源对象发生字段变化，参数为变化的字段对象。 */
    public void viewFieldChanged(AbstractViewField field);
}