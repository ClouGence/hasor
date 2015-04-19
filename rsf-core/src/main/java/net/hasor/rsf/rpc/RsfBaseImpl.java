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
package net.hasor.rsf.rpc;
import net.hasor.rsf.manager.OptionManager;
import net.hasor.rsf.protocol.protocol.ProtocolUtils;
import net.hasor.rsf.protocol.protocol.RsfSocketBlock;
/**
 * RSF请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBaseImpl {
    private OptionManager optionManager = new OptionManager();
    private byte          protocoVersion;
    private long          requestID;
    //
    public RsfBaseImpl(RsfSocketBlock block){
        byte ver=block.
        this.protocoVersion= ProtocolUtils.finalVersionForRequest(block.get));

        block
        
    }
    //
    public String[] getOptionKeys() {
        return this.optionManager.getOptionKeys();
    }
    public String getOption(String key) {
        return this.optionManager.getOption(key);
    }
    public void addOption(String key, String value) {
        this.optionManager.addOption(key, value);
    }
    public void removeOption(String key) {
        this.optionManager.removeOption(key);
    }
    /**获取协议版本。*/
    public byte getProtocol() {
        return this.protocoVersion;
    }
    /**请求ID。*/
    public long getRequestID() {
        return this.requestID;
    }
}