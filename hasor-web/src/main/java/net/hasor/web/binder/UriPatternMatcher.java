/**
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.web.binder;
/**
 * A general interface for matching a URI against a URI pattern. Guice-servlet provides regex and
 * servlet-style pattern matching out of the box.
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 * @author 赵永春 (zyc@hasor.net) 2019-01-07 ,删除extractPath方法
 */
public interface UriPatternMatcher {
    /**
     * @param uri A "contextual" (i.e. relative) Request URI, *not* a complete one.
     * @return Returns true if the uri matches the pattern.
     */
    public boolean matches(String uri);

    /** Returns the type of pattern this is. */
    public UriPatternType getPatternType();
}