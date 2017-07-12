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
package net.hasor.rsf.address.route.rule;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.core.setting.InputStreamSettings;
import net.hasor.core.setting.StreamType;
import net.hasor.rsf.RsfEnvironment;
import net.hasor.rsf.RsfSettings;
import net.hasor.rsf.utils.ReaderInputStream;
import net.hasor.rsf.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 路由规则解析器
 * @version : 2015年3月29日
 * @author 赵永春(zyc@hasor.net)
 */
public class RuleParser {
    protected Logger                logger      = LoggerFactory.getLogger(getClass());
    private   Map<String, Class<?>> ruleTypeMap = null;
    public RuleParser(RsfEnvironment rsfEnvironment) {
        this.ruleTypeMap = new HashMap<String, Class<?>>();
        RsfSettings rsfSettings = rsfEnvironment.getSettings();
        XmlNode[] flowcontrolNodes = rsfSettings.getXmlNodeArray("hasor.rsfConfig.route.flowcontrol");
        if (flowcontrolNodes != null) {
            for (XmlNode node : flowcontrolNodes) {
                List<XmlNode> ruleTypes = node.getChildren();
                ruleTypes = (ruleTypes == null) ? new ArrayList<XmlNode>(0) : ruleTypes;
                for (XmlNode ruleType : ruleTypes) {
                    String ruleID = ruleType.getName().trim().toLowerCase();
                    String ruleClassName = ruleType.getText().trim();
                    try {
                        Class<?> ruleClass = rsfEnvironment.getClassLoader().loadClass(ruleClassName);
                        ruleTypeMap.put(ruleID, ruleClass);
                    } catch (Throwable e) {
                        logger.error("rule {} load type error -> {}", ruleID, e.getMessage());
                    }
                }
            }
        }
    }
    //
    //
    /**解析规则文本为{@link Settings}*/
    public Rule ruleSettings(String rawRoute) {
        if (StringUtils.isBlank(rawRoute) || !rawRoute.startsWith("<flowControl") || !rawRoute.endsWith("</flowControl>")) {
            logger.info("rule raw format error.");
            return null;
        }
        //
        try {
            ReaderInputStream ris = new ReaderInputStream(new StringReader("<xml>" + rawRoute + "</xml>"));
            InputStreamSettings ruleSettings = new InputStreamSettings();
            ruleSettings.addStream(ris, StreamType.Xml);
            ruleSettings.loadSettings();
            return ruleSettings(ruleSettings);
        } catch (Exception e) {
            logger.error("rule raw format error. -> {}", e);
        }
        return null;
    }
    /**解析规则文本为{@link Settings}*/
    public Rule ruleSettings(Settings ruleSettings) {
        if (ruleSettings == null) {
            logger.info("ruleSettings is null.");
            return null;
        }
        //
        AbstractRule ruleObject = null;
        try {
            String ruleID = ruleSettings.getString("flowControl.type");
            boolean ruleEnable = ruleSettings.getBoolean("flowControl.enable", false);
            //
            if (StringUtils.isBlank(ruleID)) {
                logger.info("flowControl.type is null.");
                return null;
            }
            //
            ruleID = ruleID.trim().toLowerCase();
            logger.info("process rule '{}' -> {}.", ruleID, ruleEnable);
            Class<?> ruleClass = ruleTypeMap.get(ruleID);
            if (ruleClass == null) {
                logger.info("rule type of '{}' is undefined.", ruleID);
                return null;
            }
            //
            ruleObject = (AbstractRule) ruleClass.newInstance();
            ruleObject.setRouteID(ruleID);
            ruleObject.setRouteBody(ruleSettings.getXmlNode("flowControl").getXmlText());
            ruleObject.enable(ruleEnable);
            ruleObject.paserControl(ruleSettings);
            //
        } catch (Exception e) {
            logger.error("rule raw format error -> {}", e);
        }
        return ruleObject;
    }
    @Override
    public String toString() {
        return "RuleParser Types:" + ruleTypeMap.keySet();
    }
}