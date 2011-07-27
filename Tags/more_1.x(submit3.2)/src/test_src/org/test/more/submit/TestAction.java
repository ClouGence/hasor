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
package org.test.more.submit;
import org.more.submit.Action;
import org.more.submit.ActionStack;
@Action
public class TestAction {
    public Object xml(ActionStack stack) throws Throwable {
        System.out.println("hello word");
        return stack.getContext().doActionOnStack("test.test", stack, null);
    }
    public Object test(ActionStack stack) {
        System.out.println("hello word");
        return "hello";
    }
}