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
package net.hasor.core.setting;
import java.io.Reader;
import java.net.URI;
import java.net.URL;

/***
 *
 * @version : 16/7/4
 * @author 赵永春 (zyc@byshell.org)
 */
public class ConfigSource {
    private StreamType streamType;
    private URI        resourceUri;
    private URL        resourceUrl;
    private Reader     resourceReader;

    public ConfigSource(StreamType streamType, URI resourceUri) {
        this.streamType = streamType;
        this.resourceUri = resourceUri;
    }

    public ConfigSource(StreamType streamType, URL resourceUrl) {
        this.streamType = streamType;
        this.resourceUrl = resourceUrl;
    }

    public ConfigSource(StreamType streamType, Reader resourceReader) {
        this.streamType = streamType;
        this.resourceReader = resourceReader;
    }

    public StreamType getStreamType() {
        return streamType;
    }

    public URI getResourceUri() {
        return resourceUri;
    }

    public URL getResourceUrl() {
        return resourceUrl;
    }

    public Reader getResourceReader() {
        return resourceReader;
    }

    public String toString() {
        if (resourceUri != null) {
            return this.streamType.name() + "@" + resourceUri.toString();
        }
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
