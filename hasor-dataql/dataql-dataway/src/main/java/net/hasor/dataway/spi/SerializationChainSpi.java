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
package net.hasor.dataway.spi;
import net.hasor.web.MimeType;

import java.io.InputStream;
import java.util.EventListener;

/**
 * SPI 允许开发者完全控制序列化方式。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-19
 */
public interface SerializationChainSpi extends EventListener {
    public static class SerializationInfo {
        private String contentType        = null;
        private String contentDisposition = null;
        private long   contentLength      = -1;
        private Object data               = null;

        public String getContentType() {
            return this.contentType;
        }

        public String getContentDisposition() {
            return this.contentDisposition;
        }

        public long getContentLength() {
            return this.contentLength;
        }

        public Object getData() {
            return this.data;
        }

        /** 字符串数据 */
        public static SerializationInfo ofString(String contentType, String data) {
            return of(contentType, data);
        }

        /** 字节数据 */
        public static SerializationInfo ofBytes(String contentType, byte[] data) {
            return of(contentType, data, null, data.length);
        }

        /** 字节数据 */
        public static SerializationInfo ofBytes(String contentType, byte[] data, String disposition) {
            return of(contentType, data, disposition, data.length);
        }

        /** 对象,会被 json 序列化 */
        public static SerializationInfo ofObject(String contentType, Object data) {
            return of(contentType, data);
        }

        /** 输出流 */
        public static SerializationInfo ofStream(String contentType, InputStream data) {
            return of(contentType, data);
        }

        /** 输出流 */
        public static SerializationInfo ofStream(String contentType, InputStream data, long contentLength) {
            return of(contentType, data, null, contentLength);
        }

        /** 输出流 */
        public static SerializationInfo ofStream(String contentType, InputStream data, long contentLength, String disposition) {
            return of(contentType, data, disposition, contentLength);
        }

        private static SerializationInfo of(String contentType, Object data) {
            SerializationInfo info = new SerializationInfo();
            info.contentType = contentType;
            info.data = data;
            return info;
        }

        private static SerializationInfo of(String contentType, Object data, String disposition, long contentLength) {
            SerializationInfo info = new SerializationInfo();
            info.contentType = contentType;
            info.contentDisposition = disposition;
            info.contentLength = contentLength;
            info.data = data;
            return info;
        }
    }

    /**
     * 成功完成调用
     * @param apiInfo API 调用信息
     * @param mimeType mimeType 查询器，当返回 SerializationInfo 类型的时候可以协助确定 content_type
     * @param result 结果信息
     * @return 返回结果，或者抛出异常。
     */
    public Object doSerialization(ApiInfo apiInfo, MimeType mimeType, Object result);
}