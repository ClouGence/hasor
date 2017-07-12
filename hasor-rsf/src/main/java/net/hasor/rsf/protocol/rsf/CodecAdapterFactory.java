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
package net.hasor.rsf.protocol.rsf;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.protocol.rsf.v1.CodecAdapterForV1;

import static net.hasor.rsf.domain.RsfConstants.Version_1;
/**
 * Protocol Interface,for custom network protocol
 * @version : 2014年11月4日
 * @author 赵永春(zyc@hasor.net)
 */
public class CodecAdapterFactory {
    private static CodecAdapter[] adapterPool = new CodecAdapter[16];
    //
    public static CodecAdapter getCodecAdapterByVersion(RsfEnvironment rsfEnvironment, byte version) {
        if ((version | Version_1) == version) {
            if (adapterPool[Version_1] == null) {
                adapterPool[Version_1] = new CodecAdapterForV1(rsfEnvironment);
            }
            return adapterPool[Version_1];
        }
        throw new IllegalStateException("this version " + version + " does not support agreement.");
    }
}