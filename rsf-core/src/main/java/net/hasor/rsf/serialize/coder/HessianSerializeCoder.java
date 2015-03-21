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
package net.hasor.rsf.serialize.coder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import net.hasor.libs.com.caucho.hessian.io.HessianInput;
import net.hasor.libs.com.caucho.hessian.io.HessianOutput;
import net.hasor.libs.com.caucho.hessian.io.SerializerFactory;
import net.hasor.rsf.serialize.SerializeCoder;
/**
 * 
 * @version : 2014年9月19日
 * @author 赵永春(zyc@hasor.net)
 */
public class HessianSerializeCoder implements SerializeCoder {
    private SerializerFactory serializerFactory = null;
    //
    public HessianSerializeCoder() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        this.serializerFactory = new SerializerFactory(loader);
    }
    //
    public byte[] encode(Object object) throws IOException {
        ByteArrayOutputStream binary = new ByteArrayOutputStream();
        HessianOutput hout = new HessianOutput(binary);
        hout.setSerializerFactory(this.serializerFactory);
        hout.writeObject(object);
        return binary.toByteArray();
    }
    //
    public Object decode(byte[] bytes) throws IOException {
        if (bytes == null)
            return null;
        HessianInput input = new HessianInput(new ByteArrayInputStream(bytes));
        input.setSerializerFactory(this.serializerFactory);
        return input.readObject();
    }
}