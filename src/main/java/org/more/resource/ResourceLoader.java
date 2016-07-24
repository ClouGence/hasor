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
package org.more.resource;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
/**
 *
 * @version : 2013-6-6
 * @author 赵永春 (zyc@hasor.net)
 */
public interface ResourceLoader {
    public URL getResource(String resourcePath) throws IOException;

    /**装载指定资源。*/
    public InputStream getResourceAsStream(String resourcePath) throws IOException;

    /**测试资源是否存在。*/
    public boolean exist(String resourcePath) throws IOException;
}