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
/**
 *
 * @version : 2015年12月3日
 * @author 赵永春(zyc@hasor.net)
 */
public class RuleRef {
    public RuleRef(RuleRef scriptResourcesRef) {
        if (scriptResourcesRef != null) {
            this.serviceLevel = scriptResourcesRef.serviceLevel;
            this.methodLevel = scriptResourcesRef.methodLevel;
            this.argsLevel = scriptResourcesRef.argsLevel;
        }
    }
    private InnerRuleEngine serviceLevel = new InnerRuleEngine(); //服务级
    private InnerRuleEngine methodLevel  = new InnerRuleEngine(); //方法级
    private InnerRuleEngine argsLevel    = new InnerRuleEngine(); //参数级
    //
    public InnerRuleEngine getServiceLevel() {
        return serviceLevel;
    }
    public void setServiceLevel(InnerRuleEngine serviceLevel) {
        this.serviceLevel = serviceLevel;
    }
    public InnerRuleEngine getMethodLevel() {
        return methodLevel;
    }
    public void setMethodLevel(InnerRuleEngine methodLevel) {
        this.methodLevel = methodLevel;
    }
    public InnerRuleEngine getArgsLevel() {
        return argsLevel;
    }
    public void setArgsLevel(InnerRuleEngine argsLevel) {
        this.argsLevel = argsLevel;
    }
}