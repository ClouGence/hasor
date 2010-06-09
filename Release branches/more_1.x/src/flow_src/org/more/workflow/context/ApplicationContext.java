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
package org.more.workflow.context;
/**
 * 
 * Date : 2010-5-17
 * @author Administrator
 */
public class ApplicationContext {
    private FlashSession flashSession;
    /**获取硬Session，硬Session是将数据存放在磁盘文件。*/
    public FlashSession getHardSession() {
        return flashSession;
    };
    /**获取软Session，软Session是将数据存放在内存。*/
    public FlashSession getSoftSession() {
        return flashSession;
    };
};