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
package net.hasor.land.replicator;
/**
 * 数据上下文
 *
 * @version : 2016年09月10日
 * @author 赵永春(zyc@hasor.net)
 */
public class LogDataContext {
    private String commitTerm = null; //已知的,最大的,已经被提交的日志条目的termID
    //
    /** 已知的最大提交日志条目的termID */
    public String getCommitTerm() {
        return commitTerm;
    }
}