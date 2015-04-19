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
package net.hasor.rsf.rpc.objects.socket;
import net.hasor.rsf.manager.OptionManager;
import net.hasor.rsf.protocol.protocol.PoolSocketBlock;
import net.hasor.rsf.protocol.protocol.RsfSocketBlock;
import net.hasor.rsf.utils.ProtocolUtils;
/**
 * RSF请求
 * @version : 2014年10月25日
 * @author 赵永春(zyc@hasor.net)
 */
public class RsfBaseFormSocket<Context, DATA extends RsfSocketBlock> {
    private OptionManager optionManager = new OptionManager();
    private DATA          rsfBlock;
    private byte          protocoVersion;
    private long          requestID;
    private String        serializeType;
    //
    public RsfBaseFormSocket(Context context, DATA rsfBlock) {
        this.rsfBlock = rsfBlock;
        this.recovery(rsfBlock);
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
    /**请求ID。*/
    public String getSerializeType() {
        return this.serializeType;
    }
    //
    protected DATA getRsfBlock() {
        return this.rsfBlock;
    }
    //
    public void recovery(DATA rsfBlock) {
        //
        //1.基本数据
        byte ver = rsfBlock.getVersion();
        if (ProtocolUtils.isRequest(ver)) {
            this.protocoVersion = ProtocolUtils.finalVersionForRequest(ver);
        } else {
            this.protocoVersion = ProtocolUtils.finalVersionForResponse(ver);
        }
        this.requestID = rsfBlock.getRequestID();
        short serializeType = rsfBlock.getSerializeType();
        this.serializeType = new String(rsfBlock.readPool(serializeType));
        //
        //2.Opt参数
        int[] optionArray = rsfBlock.getOptions();
        for (int optItem : optionArray) {
            short optKey = (short) (optItem >>> 16);
            short optVal = (short) (optItem & PoolSocketBlock.PoolMaxSize);
            String optKeyStr = new String(rsfBlock.readPool(optKey));
            String optValStr = new String(rsfBlock.readPool(optVal));
            this.optionManager.addOption(optKeyStr, optValStr);
        }
    }
}