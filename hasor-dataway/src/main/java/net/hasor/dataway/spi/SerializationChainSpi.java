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

import java.util.EventListener;

/**
 * SPI 允许开发者完全控制序列化方式。
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2020-04-19
 */
public interface SerializationChainSpi extends EventListener {
    public static class SerializationInfo {
        private String mimeType;
        private Object data;

        public String getMimeType() {
            return this.mimeType;
        }

        public Object getData() {
            return this.data;
        }

        public static SerializationInfo of(String mimeType, Object data) {
            SerializationInfo info = new SerializationInfo();
            info.mimeType = mimeType;
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