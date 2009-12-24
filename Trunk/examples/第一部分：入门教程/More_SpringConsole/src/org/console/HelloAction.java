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
package org.console;
import org.more.submit.ActionStack;
/**
 * 作为Action类必须是共有的非抽象的，同时也不能是接口。
 * Date : 2009-12-11
 */
public class HelloAction {
    /**Action方法必须接受一个类型为ActionStack的参数，Submit对于Action的返回值不做任何限制。*/
    public Object hello(ActionStack stack) {
        System.out.println("Hello More!");
        return "这个是返回结果。";
    }
}