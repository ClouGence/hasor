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
package net.hasor.rsf.address;
import net.hasor.rsf.libs.org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;
import net.hasor.rsf.utils.CommonCodeUtils;
import net.hasor.rsf.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;
/**
 *
 * @version : 2015年12月3日
 * @author 赵永春(zyc@hasor.net)
 */
class InnerRuleEngine {
    protected static final Logger                  logger     = LoggerFactory.getLogger(InnerRuleEngine.class);
    private volatile       String                  ruleScript = null; //规则脚本
    private volatile       String                  signature  = null; //脚本内容签名，用于校验是否发生变化
    private volatile       RuleGroovyScriptFace<?> runScript  = null; //调用程序
    //
    //
    public boolean isEnable() {
        return runScript != null;
    }
    public synchronized boolean update(String ruleScript) {
        //1.空内容判断
        if (StringUtils.isBlank(ruleScript)) {
            ruleScript = "";
            if (this.ruleScript == null) {
                return false;/*将脚本更新为空，同时本地也为空 ->不执行脚本更新。*/
            }
        }
        //2.内容签名
        String signature = null;
        try {
            signature = CommonCodeUtils.MD5.getMD5(ruleScript);
        } catch (Throwable e) {
            logger.error("eval ruleScript signature error ->" + e.getMessage(), e);
            signature = ruleScript;
        }
        //2.内容是否变化
        if (signature.equalsIgnoreCase(this.signature)) {
            return false;/*无变化*/
        }
        //
        try {
            if (StringUtils.isBlank(ruleScript)) {
                this.ruleScript = null;
                this.signature = signature;
                return true;
            }
            ScriptEngine engine = new GroovyScriptEngineImpl();
            engine.eval(ruleScript);
            this.runScript = ((Invocable) engine).getInterface(RuleGroovyScriptFace.class);
            logger.info("ruleEngine ruleScript compiler finish.");
            //
            this.ruleScript = ruleScript;
            this.signature = signature;
            return true;
        } catch (Throwable e) {
            if (e instanceof ScriptException) {
                ScriptException se = (ScriptException) e;
                logger.error("ruleEngine ruleScript compiler error ->at line: " + se.getLineNumber() //
                        + " , column: " + se.getColumnNumber() + " , message:" + e.getMessage(), e);
            } else {
                logger.error("ruleEngine ruleScript compiler error ->" + e.getMessage(), e);
            }
            return false;
        }
    }
    public String getScript() {
        return this.ruleScript;
    }
    //
    public Object runRule(String serviceID, List<String> allAddress) {
        if (this.runScript == null) {
            return null;
        }
        try {
            Object result = this.runScript.evalAddress(serviceID, allAddress);
            return result;
        } catch (Throwable e) {
            logger.error("evalServiceLevel error ,message = " + e.getMessage(), e);
            return null;
        }
    }
}