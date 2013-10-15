/*
 * Copyright 2002-2007 the original author or authors.
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
package net.hasor.jdbc.jdbc.core;
import net.hasor.jdbc.jdbc.BatchPreparedStatementSetter;
/**
 * 扩展 BatchPreparedStatementSetter 接口，提供了一个方法可以中断某一个批操作。
 * @version : 2013-10-14
 * @author 赵永春(zyc@hasor.net)
 */
public interface InterruptibleBatchPreparedStatementSetter extends BatchPreparedStatementSetter {
    /**测试批处理是否继续，返回 true 表示处理。false 表示在批处理中放弃这个条目。*/
    public boolean isBatchExhausted(int i);
}