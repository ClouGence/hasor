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
package net.hasor.rsf.serialize.coder;
import hprose.io.HproseReader;
import hprose.io.HproseWriter;
import net.hasor.core.Environment;
import net.hasor.rsf.SerializeCoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @version : 2017年1月12日
 * @author 赵永春 (zyc@hasor.net)
 */
public class HproseSerializeCoder implements SerializeCoder {
    @Override
    public void initCoder(Environment environment) {
    }

    public byte[] encode(Object object) throws IOException {
        ByteArrayOutputStream binary = new ByteArrayOutputStream();
        HproseWriter writer = new HproseWriter(binary);
        writer.serialize(object);
        return binary.toByteArray();
    }

    public Object decode(byte[] bytes, Class<?> returnType) throws IOException {
        if (bytes == null) {
            return null;
        }
        HproseReader reader = new HproseReader(bytes);
        return reader.unserialize(returnType);
    }
}