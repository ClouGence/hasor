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
package net.hasor.rsf.center.server.domain.entity;
import net.hasor.rsf.domain.RsfServiceType;
/**
 * 状态
 * 
 * @version : 2015年5月22日
 * @author 赵永春(zyc@hasor.net)
 */
public enum TerminalTypeEnum {
    /**提供者*/
    Provider('P'),
    /**消费者*/
    Consumer('C');
    //
    private char personaType = 0;
    TerminalTypeEnum(char personaType) {
        this.personaType = personaType;
    }
    public char getPersonaType() {
        return personaType;
    }
    //
    public static TerminalTypeEnum forRsfServiceTypeEnum(RsfServiceType type) {
        switch (type) {
        case Provider:
            return TerminalTypeEnum.Provider;
        case Consumer:
            return TerminalTypeEnum.Consumer;
        default:
            return null;
        }
    }
}