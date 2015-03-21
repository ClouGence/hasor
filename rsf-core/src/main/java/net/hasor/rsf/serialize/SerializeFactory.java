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
import net.hasor.core.XmlNode;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.constants.RsfException;
/**
 * 序列化工厂
 * @version : 2014年9月20日
 * @author 赵永春(zyc@hasor.net)
 */
public class SerializeFactory {
    private Map<String, SerializeCoder> coderMap = new HashMap<String, SerializeCoder>();
    //
    /**获取序列化（编码/解码）器。*/
    public SerializeCoder getSerializeCoder(String codeName) {
        return this.coderMap.get(codeName);
    }
    /**注册序列化（编码/解码）器*/
    public void registerSerializeCoder(String codeName, SerializeCoder decoder) {
        this.coderMap.put(codeName, decoder);
    }
    //
    //
    public static SerializeFactory createFactory(RsfSettings settings) {
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
        String serializeCoder = atNode.getText().trim();
        //
        try {
            SerializeCoder coder = (SerializeCoder) Class.forName(serializeCoder).newInstance();
            factory.registerSerializeCoder(serializeType, coder);
        } catch (Exception e) {
            throw new RsfException((short) 0, e);
        }
    }
}