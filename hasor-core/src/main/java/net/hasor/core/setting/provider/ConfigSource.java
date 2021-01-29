/*
 *
 *  * Copyright 2008-2016 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package net.hasor.core.setting.provider;
import java.io.Reader;
import java.net.URL;

/***
 *
 * @version : 16/7/4
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConfigSource {
    private final String     namespace;
    private final StreamType streamType;
    private final URL        resourceUrl;
    private final Reader     resourceReader;

    public ConfigSource(String namespace, StreamType streamType, URL resourceUrl) {
        this.namespace = namespace;
        this.streamType = streamType;
        this.resourceUrl = resourceUrl;
        this.resourceReader = null;
    }

    public ConfigSource(String namespace, StreamType streamType, Reader resourceReader) {
        this.namespace = namespace;
        this.streamType = streamType;
        this.resourceUrl = null;
        this.resourceReader = resourceReader;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public StreamType getStreamType() {
        return streamType;
    }

    public URL getResourceUrl() {
        return resourceUrl;
    }

    public Reader getResourceReader() {
        return resourceReader;
    }

    public String toString() {
        if (resourceUrl != null) {
            return this.streamType.name() + "@" + resourceUrl.toString();
        }
        if (resourceReader != null) {
            return this.streamType.name() + "@Reader-" + resourceReader.toString();
        }
        return this.streamType.name() + "@none";
    }

    public boolean equals(Object obj) {
        if (obj instanceof ConfigSource) {
            return this.toString().equals(obj.toString());
        }
        return false;
    }
}
