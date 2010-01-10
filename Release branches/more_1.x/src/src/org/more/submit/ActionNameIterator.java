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
package org.more.submit;
import java.util.Iterator;
/**
 * Action名称过滤器资源迭代器，该迭代器测试每一个来自ActionObjectFactory名称迭代器的名称以过滤所有不是action的名称。
 * @version 2010-1-9
 * @author 赵永春 (zyc@byshell.org)
 */
class ActionNameIterator implements Iterator<String> {
    private Iterator<String> objectNameIterator;
    private ActionContext    context;
    public ActionNameIterator(Iterator<String> objectNameIterator, ActionContext context) {
        this.objectNameIterator = objectNameIterator;
        this.context = context;
    }
    @Override
    public boolean hasNext() {
        return objectNameIterator.hasNext();
    }
    @Override
    public String next() {
        while (hasNext()) {
            String objectName = this.objectNameIterator.next();
            if (context.containsAction(objectName) == true)
                return objectName;
        }
        return null;
    }
    @Override
    public void remove() {
        this.objectNameIterator.remove();
    }
}