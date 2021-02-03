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
package net.hasor.rsf.serialize;
import net.hasor.core.Environment;
import net.hasor.core.setting.SettingNode;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.SerializeCoder;
import net.hasor.rsf.domain.ProtocolStatus;
import net.hasor.rsf.domain.RsfException;
import net.hasor.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 序列化工厂
 * @version : 2014年9月20日
 * @author 赵永春 (zyc@hasor.net)
 */
public class SerializeFactory {
    protected static Logger                      logger   = LoggerFactory.getLogger(SerializeFactory.class);
    private          Map<String, SerializeCoder> coderMap = new HashMap<>();

    /**获取序列化（编码/解码）器。*/
    public SerializeCoder getSerializeCoder(String codeName) {
        return this.coderMap.get(codeName);
    }

    /**注册序列化（编码/解码）器*/
    public void registerSerializeCoder(String codeName, SerializeCoder decoder) {
        this.coderMap.put(codeName, decoder);
    }

    public static SerializeFactory createFactory(RsfEnvironment environment) {
        SerializeFactory factory = new SerializeFactory();
        SettingNode[] serList = environment.getSettings().getNodeArray("hasor.rsfConfig.serializeType.serialize");
        //
        String types = "";
        for (SettingNode s : serList) {
            initSerialize(factory, s, environment);
            types += ("," + s.getSubValue("name"));
        }
        if (!StringUtils.isBlank(types)) {
            types = types.substring(1);
        }
        logger.info("SerializeFactory init. -> [{}]", types);
        return factory;
    }

    private static void initSerialize(SerializeFactory factory, SettingNode atNode, Environment environment) {
        String serializeType = atNode.getSubValue("name");
        String serializeCoder = atNode.getValue().trim();
        //
        try {
            Class<?> aClass = environment.getClassLoader().loadClass(serializeCoder);
            SerializeCoder coder = (SerializeCoder) aClass.newInstance();
            coder.initCoder(environment);
            factory.registerSerializeCoder(serializeType, coder);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RsfException(ProtocolStatus.SerializeError, e);
        }
    }
}
