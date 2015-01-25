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
package net.hasor.search.client.rsf;
import java.lang.annotation.Annotation;
import org.more.builder.ReflectionToStringBuilder;
import org.more.builder.ToStringStyle;
/**
 * 事务控制
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
public class CommitInfo implements Commit {
    @Override
    public Class<? extends Annotation> annotationType() {
        return CommitInfo.class;
    }
    //
    private boolean waitFlush;
    private boolean waitSearcher;
    private boolean softCommit;
    //
    public CommitInfo() {
        this(true, true, false);
    }
    public CommitInfo(boolean waitFlush) {
        this(waitFlush, true, false);
    }
    public CommitInfo(boolean waitFlush, boolean waitSearcher) {
        this(waitFlush, waitSearcher, false);
    }
    public CommitInfo(boolean waitFlush, boolean waitSearcher, boolean softCommit) {
        this.waitFlush = waitFlush;
        this.waitSearcher = waitSearcher;
        this.softCommit = softCommit;
    }
    @Override
    public boolean softCommit() {
        return this.softCommit;
    }
    @Override
    public boolean waitFlush() {
        return this.waitFlush;
    }
    @Override
    public boolean waitSearcher() {
        return this.waitSearcher;
    }
    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}