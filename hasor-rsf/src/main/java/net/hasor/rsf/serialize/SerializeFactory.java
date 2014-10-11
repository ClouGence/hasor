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
package net.hasor.rsf.serialize;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
/**
 * 序列化工厂
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class SerializeFactory {
    private Map<String, Decoder> decoderMap = new HashMap<String, Decoder>();
    private Map<String, Encoder> encoderMap = new HashMap<String, Encoder>();
    //
    /**获取序列化解码器（Decoder）*/
    public Decoder getDecoder(String codeName) {
        codeName = codeName.toLowerCase();
        return this.decoderMap.get(codeName);
    }
    /**获取序列化编码器（Encoder）*/
    public Encoder getEncoder(String codeName) {
        codeName = codeName.toLowerCase();
        return this.encoderMap.get(codeName);
    }
    /**注册序列化解码器（Decoder）*/
    public void registerDecoder(String codeName, Decoder decoder) {
        this.decoderMap.put(codeName, decoder);
    }
    /**注册序列化编码器（Encoder）*/
    public void registerEncoder(String codeName, Encoder encoder) {
        this.encoderMap.put(codeName, encoder);
    }
    //
    //
    public static SerializeFactory createFactory(Settings settings) {
        SerializeFactory factory = new SerializeFactory();
        XmlNode[] atNode = settings.getXmlNodeArray("hasor.rsfConfig.serializeType");
        //
        for (XmlNode e : atNode) {
            List<XmlNode> serList = e.getChildren("serialize");
            for (XmlNode s : serList) {
                initSerialize(factory, s);
            }
        }
        return factory;
    }
    private static void initSerialize(SerializeFactory factory, XmlNode atNode) {
        String serializeType = atNode.getAttribute("name");
        serializeType = serializeType.toLowerCase();
        String serializeEncoder = atNode.getChildren("encoder").get(0).getText().trim();
        String serializeDecoder = atNode.getChildren("decoder").get(0).getText().trim();
        //
        try {
            Decoder decoder = (Decoder) Class.forName(serializeEncoder).newInstance();
            Encoder encoder = (Encoder) Class.forName(serializeDecoder).newInstance();
            factory.encoderMap.put(serializeType, encoder);
            factory.decoderMap.put(serializeType, decoder);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
}