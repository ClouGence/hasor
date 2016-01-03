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
package net.hasor.plugins.templates;
import java.io.IOException;
import java.io.Reader;
import net.hasor.plugins.resource.ResourceLoader;
/**
 * 
 * @version : 2012-5-15
 * @author 赵永春 (zyc@byshell.org)
 */
public interface TemplateLoader extends ResourceLoader {
    /** Finds the template in the backing storage and returns an object that identifies the storage location where the template can be loaded from. See the return value for more information.*/
    public Object findTemplateSource(String name) throws IOException;
    /** Returns the time of last modification of the specified template source. */
    public long getLastModified(Object templateSource);
    /** Returns the character stream of a template represented by the specified template source. */
    public Reader getReader(Object templateSource, String encoding) throws IOException;
    /** Closes the template source */
    public void closeTemplateSource(Object templateSource) throws IOException;
}