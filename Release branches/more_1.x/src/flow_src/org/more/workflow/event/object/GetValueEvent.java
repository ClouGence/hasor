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
package org.more.workflow.event.object;
import org.more.workflow.event.Event;
import org.more.workflow.event.EventPhase;
/**
 * 当有属性被读取时时引发。读取到的属性可以通过getResult方法获得到。
 * 如果想改变获取到的值内容则可以通过setResult方法来替换返回值。
 * Date : 2010-5-21
 * @author 赵永春
 */
public class GetValueEvent extends Event {
    /**  */
    private static final long serialVersionUID = 5010075302608463391L;
    private Object            result           = null;                 ;
    public GetValueEvent(Object target, Object result) {
        super("GetValueEvent", target);
        this.result = result;
    }
    @Override
    protected EventPhase[] createEventPhase() {
        return null;
    };
    /**读取到的属性可以通过getResult方法获得到。*/
    public Object getResult() {
        return this.result;
    };
    /**如果想改变获取到的值内容则可以通过setResult方法来替换返回值。*/
    public void setResult(Object result) {
        this.result = result;
    };
};