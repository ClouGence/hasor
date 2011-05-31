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
package org.more.hypha;
import org.more.core.error.MoreActionException;
import org.more.hypha.Event.Sequence;
/**
 * 执行事件处理期间发生异常。
 * @version : 2011-5-18
 * @author 赵永春 (zyc@byshell.org)
 */
public class EventException extends MoreActionException {
    private static final long serialVersionUID = 2713643142641891738L;
    private Sequence          sequence         = null;
    /** 执行事件处理期间发生异常。*/
    public EventException(Sequence sequence, String string) {
        super(string);
        this.sequence = sequence;
    };
    /** 执行事件处理期间发生异常。*/
    public EventException(Sequence sequence, Throwable error) {
        super(error);
        this.sequence = sequence;
    };
    /** 执行事件处理期间发生异常。*/
    public EventException(Sequence sequence, String string, Throwable error) {
        super(string, error);
        this.sequence = sequence;
    }
    public Sequence getSequence() {
        return sequence;
    };
}