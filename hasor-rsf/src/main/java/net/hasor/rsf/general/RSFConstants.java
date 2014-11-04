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
package net.hasor.rsf.general;
import java.nio.charset.Charset;
/**
 * 
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public interface RSFConstants {
    public static final byte    RSF_V_1_0_Req   = (byte) (0xC0 | ProtocolVersion.V_1_0.value());
    public static final byte    RSF_V_1_0_Res   = (byte) (0x80 | ProtocolVersion.V_1_0.value());
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final int     ClientTimeout   = 6000;                                         //（毫秒）
}