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
package net.hasor.search.client;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 事务控制
 * @version : 2015年1月8日
 * @author 赵永春(zyc@hasor.net)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Commit {
    /**waitFlush block until index changes are flushed to disk*/
    public boolean waitFlush() default true;
    /**waitSearcher block until a new searcher is opened and registered as the main query searcher, making the changes visible.*/
    public boolean waitSearcher() default false;
    /**waitSearcher block until a new searcher is opened and registered as the main query searcher, making the changes visible.*/
    public boolean softCommit() default false;
}