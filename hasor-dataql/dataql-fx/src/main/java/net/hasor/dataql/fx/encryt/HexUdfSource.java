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
package net.hasor.dataql.fx.encryt;
import net.hasor.core.Singleton;
import net.hasor.dataql.UdfSourceAssembly;
import net.hasor.dataql.fx.basic.ConvertUdfSource;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 十六进制转换函数库。函数库引入 <code>import 'net.hasor.dataql.fx.encryt.HexUdfSource' as hex;</code>
 * @author 赵永春 (zyc@hasor.net)
 * @version : 2019-12-12
 */
@Singleton
@Deprecated
public class HexUdfSource implements UdfSourceAssembly {
    /** 将二进制数据转换为 16进制字符串 */
    public static String byteToHex(List<Byte> content) {
        return ConvertUdfSource.byteToHex(content);
    }

    /** 将二进制数据转换为 16进制字符串 */
    public static byte[] hexToByte(String content) {
        return ConvertUdfSource.hexToByte(content);
    }

    /** 二进制数据转换为字符串 */
    public static String byteToString(List<Byte> content, String charset) {
        return ConvertUdfSource.byteToString(content, charset);
    }

    /** 字符串转换为二进制数据 */
    public static byte[] stringToByte(String content, String charset) throws UnsupportedEncodingException {
        return ConvertUdfSource.stringToByte(content, charset);
    }
}
