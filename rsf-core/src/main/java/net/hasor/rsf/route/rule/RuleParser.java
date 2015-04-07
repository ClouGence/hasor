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
package net.hasor.rsf.route.rule;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hasor.core.Settings;
import net.hasor.core.XmlNode;
import net.hasor.core.setting.InputStreamSettings;
import net.hasor.rsf.RsfSettings;
import org.more.logger.LoggerHelper;
import org.more.util.ClassUtils;
import org.more.util.StringUtils;
import org.more.util.io.input.ReaderInputStream;
/**
 * 路由规则解析器
 * @version : 2015年3月29日
 * @author 赵永春(zyc@hasor.net)
 */
public class RuleParser {
    private Map<String, Class<?>> ruleTypeMap = null;
    public RuleParser(RsfSettings rsfSettings) {
        this.ruleTypeMap = new HashMap<String, Class<?>>();
        XmlNode[] flowcontrolNodes = rsfSettings.getXmlNodeArray("hasor.rsfConfig.route.flowcontrol");
        if (flowcontrolNodes != null) {
            for (XmlNode node : flowcontrolNodes) {
                List<XmlNode> ruleTypes = node.getChildren();
                ruleTypes = (ruleTypes == null) ? new ArrayList<XmlNode>(0) : ruleTypes;
                for (XmlNode ruleType : ruleTypes) {
                    String ruleID = ruleType.getName().trim().toLowerCase();
                    String ruleClassName = ruleType.getText().trim();
                    try {
                        Class<?> ruleClass = ClassUtils.getClass(ruleClassName);
                        ruleTypeMap.put(ruleID, ruleClass);
                    } catch (Throwable e) {
                        LoggerHelper.logSevere("rule %s load type error -> %s", ruleID, e.getMessage());
                    }
                }
            }
        }
    }
    //
    /**解析规则文本为{@link Settings}*/
    public Rule ruleSettings(String rawRoute) {
        if (StringUtils.isBlank(rawRoute) || !StringUtils.startsWithIgnoreCase(rawRoute, "<flowControl") || !StringUtils.endsWithIgnoreCase(rawRoute, "</flowControl>")) {
            LoggerHelper.logConfig("rule raw format error.");
            return null;
        }
        //
        AbstractRule ruleObject = null;
        try {
            ReaderInputStream ris = new ReaderInputStream(new StringReader("<xml>" + rawRoute + "</xml>"));
            InputStreamSettings ruleSettings = new InputStreamSettings(ris);
            ruleSettings.loadSettings();
            String ruleID = ruleSettings.getString("flowControl.type");
            boolean ruleEnable = ruleSettings.getBoolean("flowControl.enable", false);
            //
            if (StringUtils.isBlank(ruleID)) {
                LoggerHelper.logConfig("flowControl.type is null.");
                return null;
            }
            //
            ruleID = ruleID.trim().toLowerCase();
            LoggerHelper.logConfig("process rule '%s' -> %s.", ruleID, ruleEnable);
            Class<?> ruleClass = ruleTypeMap.get(ruleID);
            if (ruleClass == null) {
                LoggerHelper.logConfig("rule type of '%s' is undefined.", ruleID);
                return null;
            }
            //
            ruleObject = (AbstractRule) ruleClass.newInstance();
            ruleObject.setRouteID(ruleID);
            ruleObject.setRoutebody(rawRoute);
            ruleObject.enable(ruleEnable);
            ruleObject.paserControl(ruleSettings);
            //
        } catch (Exception e) {
            LoggerHelper.logConfig("rule raw format error.", e);
        }
        return ruleObject;
    }
    @Override
    public String toString() {
        return "RuleParser Types:" + ruleTypeMap.keySet();
    }
}