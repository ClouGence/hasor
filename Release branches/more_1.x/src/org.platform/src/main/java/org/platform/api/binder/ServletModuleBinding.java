/**
 * Copyright (C) 2010 Google Inc.
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
package org.platform.api.binder;
import java.util.Map;
/**
* A binding created by .
* @author sameb@google.com (Sam Berlin)
* @since 3.0
*/
interface ServletModuleBinding {
    /** Returns the pattern type that this binding was created with. */
    public UriPatternType getUriPatternType();
    /** Returns the pattern used to match against the binding. */
    public String getPattern();
    /** Returns any context params supplied when creating the binding. */
    public Map<String, String> getInitParams();
    /** Returns true if the given URI will match this binding. */
    public boolean matchesUri(String uri);
}