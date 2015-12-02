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
package net.hasor.rsf.address;
import java.security.NoSuchAlgorithmException;
import javax.script.Invocable;
import org.more.util.CommonCodeUtils.MD5;
import org.more.util.StringUtils;
class RefRule {
    public RefRule() {}
    public RefRule(RefRule scriptResourcesRef) {
        this.serviceLevel = scriptResourcesRef.serviceLevel;
        this.methodLevel = scriptResourcesRef.methodLevel;
        this.argsLevel = scriptResourcesRef.argsLevel;
    }
    public RuleEngine serviceLevel = new RuleEngine(); //服务级
    public RuleEngine methodLevel  = new RuleEngine(); //方法级
    public RuleEngine argsLevel    = new RuleEngine(); //参数级
}
//
//
class RuleEngine {
    private String    ruleScript   = null; //规则脚本
    private String    md5Signature = null; //MD5签名
    private Invocable scriptEngine = null; //调用程序
    //
    public boolean isEnable() {
        return StringUtils.isBlank(ruleScript);
    }
    public void update(String ruleScript) {
        if (StringUtils.isBlank(ruleScript)) {
            clean();
            return;
        }
        String ruleSignature;
        try {
            ruleSignature = MD5.getMD5(ruleScript);
        } catch (NoSuchAlgorithmException e) {
            ruleSignature = ruleScript;
        }
        if (StringUtils.equalsIgnoreCase(md5Signature, ruleSignature)) {
            return;/*不需要变化*/
        }
        clean();
        this.ruleScript = ruleScript;
    }
    private void clean() {
        this.scriptEngine = null;
    }
    public String getScript() {
        return this.ruleScript;
    }
}